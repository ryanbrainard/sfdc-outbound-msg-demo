package com.salesforce.caseinformer;

import com.sforce.soap._2005._09.outbound.AccountNotification;
import com.sforce.soap.enterprise.sobject.Account;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class DefaultTestAccount extends TestAccount {

    @Override
    public String getName() {
        return "RELEASE_NAME";
    }

    @Override
    public Release.Status getStatus() {
        return Release.Status.DEPLOYED_SUCCESSFULLY;
    }

    @Override
    public AccountNotification getNotification() {
        return new AccountNotification("NOTIFICATION_ID_1", new Account(null, getId(), getName()));
    }

    @Override
    public InputStream getReleaseInfoQueryResponse() throws FileNotFoundException {
        return Resources.asStream("gusReleaseInfoQueryResult.xml");
    }

    @Override
    public InputStream[] getWorkInfoQueryResponses() throws FileNotFoundException {
        return new InputStream[]{
                Resources.asStream("gusWorkInfoQueryResult_1.xml"),
                Resources.asStream("gusWorkInfoQueryResult_2.xml"),
        };
    }

    @Override
    public InputStream[] getCommentSaveRequests() throws FileNotFoundException {
        return new InputStream[]{
               Resources.asStream("createCaseCommentRequest.xml")
        };
    }

    @Override
    public InputStream[] getCommentSaveResponses() throws FileNotFoundException {
       return new InputStream[]{
               Resources.asStream("createCaseCommentResponse.xml")
        };
    }

    @Override
    public Release make() {
        final Release release = new Release(getId());
        final Case caseWithOneBugAttached = makeCaseWithOneBugAttached();
        final Case caseWithTwoBugsAttached = makeCaseWithTwoBugsAttached();
        final Work bugAffectingOneCustomer = makeBugAffectingOneCustomer();
        final Work bugAffectingTwoCustomers = makeBugAffectingTwoCustomers();

        release.setName(getName());

        release.addCase(caseWithOneBugAttached);
        caseWithOneBugAttached.addWorkItem(bugAffectingTwoCustomers);

        release.addCase(caseWithTwoBugsAttached);
        caseWithTwoBugsAttached.addWorkItem(bugAffectingOneCustomer);
        caseWithTwoBugsAttached.addWorkItem(bugAffectingTwoCustomers);

        return release;
    }

    private Work makeBugAffectingOneCustomer() {
        return new Work("W-000001", "A Change Sets bug affecting one customer");
    }

    private Work makeBugAffectingTwoCustomers() {
        return new Work("W-000002",
                "A Sandbox bug affecting two customers");
    }

    private Case makeCaseWithOneBugAttached() {
        return new Case("50030000007sAiW",
                "00000001",
                "Help, my Sandbox won't finish copying",
                "Genentech, Inc.*",
                "Graham Clarke");
    }

    private Case makeCaseWithTwoBugsAttached() {
        return new Case("50030000007sq87",
                "00000002",
                "Sandbox never stopped copying and Change Sets is broken",
                "Merrill Lynch",
                "Evan Angeles");
    }
}
