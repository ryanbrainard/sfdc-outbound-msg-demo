package com.salesforce.caseinformer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Adds comments about a given release to associated cases
 */
public class CaseCommenter implements ReleaseAnnouncementAction {

    private final static Logger logger = LoggerFactory.getLogger(CaseCommenter.class);

    private final PartnerConnection org62Connection;

    public CaseCommenter(PartnerConnection org62Connection) {
        this.org62Connection = org62Connection;
    }

    /**
     *  Maps comment to execute as an ReleaseAnnouncementAction
     * @param release
     * @throws ConnectionException
     */
    @Override
    public void execute(Release release) throws ConnectionException {
        comment(release);
    }

    /**
     * Adds comments about the given release to associated cases.
     * Handles preparing and saving comments to the 62 Org, including
     * chunking into batches, if needed. Save results are appended
     * to the release argument and returned.
     *
     *
     * @param release a populated release object
     * @throws ConnectionException
     */
    public void comment(Release release) throws ConnectionException {
        if (release == null) {
            throw new IllegalArgumentException("Release must not be null");
        }

        saveComments(prepareComments(release));
        logger.info("Commenting completed for release " + release.getName());
    }

    List<Map.Entry<SObject, Case>> prepareComments(Release release) {
        final Map<SObject, Case> commentsToCases = Maps.newLinkedHashMap(); // linked to keep order consistent for testing

        for (final Case c : release.getCases()) {
            assert c.getCaseCommentSaveResult() == null : "Case should not have been commented yet";

            final SObject newCaseComment = new SObject();

            newCaseComment.setType("CaseComment");
            newCaseComment.setField("ParentId", c.getId());
            final StringBuilder commentBody = new StringBuilder();
            commentBody.append("Fixes for the following bugs have been released to production in ").append(release.getName()).append(":\n\n");
            for (Work w : c.getWorkItems()) {
                commentBody.append(" * ").append(w.getNumber()).append(" : ").append(w.getSubject()).append("\n");
            }
            newCaseComment.setField("CommentBody", commentBody.toString());

            commentsToCases.put(newCaseComment, c);
        }
        return Lists.newArrayList(commentsToCases.entrySet());
    }

    void saveComments(final List<Map.Entry<SObject, Case>> unsavedCaseToComments) throws ConnectionException {
        final int MAX_BATCH_SIZE = 200;
        final int batchSize = Math.min(MAX_BATCH_SIZE, unsavedCaseToComments.size());

        final SObject[] batchCreateRequest = new SObject[batchSize];
        for (int i = 0; i < batchSize; i++) {
            batchCreateRequest[i] = unsavedCaseToComments.get(i).getKey();
        }

        final SaveResult[] batchCreateResult = org62Connection.create(batchCreateRequest);

        assert batchSize == batchCreateResult.length : "Response should be same size as batch size";
        for (int i = 0; i < batchSize; i++) {
            final Case c = unsavedCaseToComments.remove(0).getValue();
            final SaveResult saveResult = batchCreateResult[i];

            c.setCaseCommentSaveResult(saveResult);

            if (!saveResult.isSuccess()) {
                logger.error("Errors updating Case " + c.getNumber() + " (" + c.getId() + ")\n" + Arrays.toString(saveResult.getErrors()));
            }
        }

        if (unsavedCaseToComments.size() > 0) {
            saveComments(unsavedCaseToComments);
        }
    }
}
