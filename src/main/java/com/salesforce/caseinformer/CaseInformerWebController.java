package com.salesforce.caseinformer;

import com.sforce.soap.partner.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"UnusedDeclaration"})
public class CaseInformerWebController {
    private static final Logger logger = LoggerFactory.getLogger(CaseInformerWebController.class);
    private static final String CASE_TREE_NAME = "caseTree";

    private String releaseId;
    private StringBuilder result = new StringBuilder();

    public void execute() {
        try {
            if (!isValidRequest()) {
                return;
            }

            final Release release = new ReleaseAnnouncer().announce(releaseId);

            appendReleaseResults(release);
        } catch (IllegalArgumentException e) {
            handleException(e, true);
        } catch (Exception e) {
            handleException(e, false);
        }
    }

    private boolean isValidRequest() {
        return null != releaseId;
    }

    private void appendReleaseResults(final Release release) {
        if (release.getCases().size() == 0) {
            result.append("No cases found associated with this release.");
            return;
        }

        result.append("<h2>").append(release.getName()).append(" Result</h2>");

        result.append("<p>Found ").append(release.getCases().size()).append(" cases with bug fixes in ")
                .append(release.getName()).append(". See below for individual results.</p>");

        result.append("<a href=\"javascript:ddtreemenu.flatten('").append(CASE_TREE_NAME)
                .append("', 'expand')\">Expand All</a> | <a href=\"javascript:ddtreemenu.flatten('")
                .append(CASE_TREE_NAME).append("', 'collapse')\">Collapse All</a>");
        result.append("<ul id='").append(CASE_TREE_NAME).append("' class='treeview'>");
        for (Case c : release.getCases()) {
            result.append("<li>Case ").append(c.getNumber()).append(": ").append(c.getSubject()).append("<ul>");

            for (Work w : c.getWorkItems()) {
                result.append("<li>").append(w.getNumber()).append(": ").append(w.getSubject()).append("</li>");
            }

            result.append("<li>");
            if (c.getCaseCommentSaveResult().isSuccess()) {
                result.append(styleSuccess("Successful"));
            } else if (c.getCaseCommentSaveResult().getErrors().length == 1) {
                result.append(styleError("Error: " + c.getCaseCommentSaveResult().getErrors()[0].getMessage()));
            } else {
                result.append(styleError("Errors:<ul>"));
                for (Error e : c.getCaseCommentSaveResult().getErrors()) {
                    result.append("<li>").append(e.getMessage()).append("</li>");
                }
                result.append("</ul>");
            }
            result.append("</li>").append("</ul></li>");
        }
        result.append("</ul>");
    }

    private void handleException(final Exception e, final boolean known) {
        if (known) {
            logger.warn(e.getClass().getSimpleName(), e);
            result.append(styleError(e.getLocalizedMessage()));
        } else {
            logger.error(e.getClass().getSimpleName(), e);
            result.append("UNKNOWN ERROR: ").append(styleError(e.toString()));
        }
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getCaseTreeName() {
        return CASE_TREE_NAME;
    }

    public String getResult() {
        return result.toString();
    }

    private static String styleSuccess(String html) {
        return "<span class='success'>" + html + "</span>";
    }

    private static String styleError(String html) {
        return "<span class='error'>" + html + "</span>";
    }
}
