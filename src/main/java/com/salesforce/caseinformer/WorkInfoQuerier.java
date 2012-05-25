package com.salesforce.caseinformer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Queries GUS for information about releases, including work items and cases.
 */
public class WorkInfoQuerier implements ReleaseAnnouncementAction {
    private static final String GUS_WORK_TO_CASE_RELATIONSHIP = "R00NT0000000lhZtMAI__r";
    private static final Logger logger = LoggerFactory.getLogger(WorkInfoQuerier.class);

	private PartnerConnection gusConnection;

    public WorkInfoQuerier(PartnerConnection gusConnection) {
        this.gusConnection = gusConnection;
    }

    @Override
    public void execute(Release release) throws ConnectionException {
        query(release);
    }

    /**
     * Queries GUS for a given Release.
     * Handles queryMore, parsing of work items and cases,
     * and construction of well-formed Release object
     *
     * @param release
     * @return Release object with child cases and work items
     * @throws com.sforce.ws.ConnectionException
     */
    public void query(final Release release) throws ConnectionException {
        Preconditions.checkNotNull(release.getId());
        Preconditions.checkArgument(release.getCases().size() == 0);

        final String soql = "SELECT " +
                                     "Name, " +
                                     "Subject__c, " +
                                     "(SELECT " +
                                         "Case_ID__c, " +
                                         "Case_Number__c, " +
                                         "Account_Name__c, " +
                                         "Case_Subject__c, " +
                                         "Case_Owner__c " +
                                       "FROM " + GUS_WORK_TO_CASE_RELATIONSHIP + ") " +
                                 "FROM ADM_Work__c " +
                                 "WHERE Id IN (SELECT Work__c FROM ADM_Released_In__c WHERE Release__c = '" + release.getId() + "') " +
                                 "AND Id IN (SELECT Work__c FROM ADM_Case__c)";

        logger.debug("Prepared SOQL query:\n" + soql);

        QueryResult queryResult = gusConnection.query(soql);
        addWorkInfoQueryResultToRelease(queryResult, release);

        while(!queryResult.isDone()){
            queryResult = gusConnection.queryMore(queryResult.getQueryLocator());
            addWorkInfoQueryResultToRelease(queryResult, release);
        }
    }

    /**
     * Parses the query result to add cases and work items to the given release in the order of:
     * Release --< Case --< Work
     * where there are no duplicate cases, but the same work item can be associated with multiple cases.
     *
     * Note, the query result is originally in the order of:
     * Release --< Work --< Case
     *
     * @param queryResult one of possibly many source QueryResults to append to the Release
     * @param release target Release
     */
    private static void addWorkInfoQueryResultToRelease(final QueryResult queryResult, final Release release) {
        for(final SObject admWork : queryResult.getRecords()) {
            final Work work = new Work((String) admWork.getField("Name"), (String) admWork.getField("Subject__c"));

            for (final Case newCase : extractChildCases(admWork)) {
                final Case c = release.hasCase(newCase.getId()) ? release.getCase(newCase.getId()) : newCase;
                c.addWorkItem(work);
                release.addCase(c);
            }
        }
    }

    /**
     * Extracts the child Cases from a given Work sObject
     *
     * @param admWork work sObject
     * @return unmodifiable collection of cases
     */
	private static Collection<Case> extractChildCases(final SObject admWork) {
        final Collection<Case> cases = Lists.newArrayList();

        final Iterator<XmlObject> admCases = admWork.getChild(GUS_WORK_TO_CASE_RELATIONSHIP).getChildren();
        while (admCases.hasNext()) {
            final XmlObject admCase = admCases.next();
            if ("records".equalsIgnoreCase(admCase.getName().getLocalPart())) {
                final String caseId = (String) admCase.getField("Case_ID__c");
                final String caseNumber = (String) admCase.getField("Case_Number__c");
                final String accountName = (String) admCase.getField("Account_Name__c");
                final String caseSubject = (String) admCase.getField("Case_Subject__c");
                final String caseOwner = (String) admCase.getField("Case_Owner__c");

                cases.add(new Case(caseId, caseNumber, caseSubject, accountName, caseOwner));
            }
        }

        return Collections.unmodifiableCollection(cases);
    }
}
