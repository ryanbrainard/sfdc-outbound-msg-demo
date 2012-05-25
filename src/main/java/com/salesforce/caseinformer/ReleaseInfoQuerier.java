package com.salesforce.caseinformer;

import com.google.common.base.Preconditions;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Queries GUS for information about releases.
 */
public class ReleaseInfoQuerier implements ReleaseAnnouncementAction {
    private static final Logger logger = LoggerFactory.getLogger(ReleaseInfoQuerier.class);

	private PartnerConnection gusConnection;

    public ReleaseInfoQuerier(PartnerConnection gusConnection) {
        this.gusConnection = gusConnection;
    }

    @Override
    public void execute(Release release) throws ConnectionException {
        query(release);
    }

    /**
     * Queries GUS for a given Release to obtain additional intormation.
     *
     * @param release
     * @return Release object with child cases and work items
     * @throws ConnectionException
     */
    public void query(final Release release) throws ConnectionException {
        Preconditions.checkNotNull(release.getId());
        Preconditions.checkArgument(release.getName() == null);

        final String releaseInfoSoql = "SELECT Name, Status__c FROM ADM_Release__c WHERE Id = '" + release.getId() + "'";

        logger.debug("Prepared SOQL query:\n" + releaseInfoSoql);

        QueryResult releaseInfoQueryResult = gusConnection.query(releaseInfoSoql);
        if (releaseInfoQueryResult.getRecords().length != 1) {
            throw new IllegalArgumentException("Release not found");
        }

        final SObject admRelease = releaseInfoQueryResult.getRecords()[0];
        release.setName((String) admRelease.getField("Name"));
        release.setStatus((String) admRelease.getField("Status__c"));
    }
}
