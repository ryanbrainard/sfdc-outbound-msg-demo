package com.salesforce.caseinformer;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.transport.HttpTransportInterceptor;
import junit.framework.TestCase;

public class ReleaseInfoQuerierTest extends TestCase {

    private TestAccount testAccount;
    private PartnerConnection mockedGusConn;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        testAccount = new DefaultTestAccount();

        mockedGusConn = new MockedConnectionLocator().getConnection();
        HttpTransportInterceptor.reset();
    }

    /**
     * Release names that provide one and only one release should return the expected release id
     * by sending the expected SOQL query to GUS
     */
    public void testQuery() throws Exception {
        HttpTransportInterceptor.enqueueMockResponses(testAccount.getReleaseInfoQueryResponse());

        final Release release = new Release(testAccount.getId());
        new ReleaseInfoQuerier(mockedGusConn).query(release);
        assertEquals(testAccount.getName(), release.getName());
        assertEquals(testAccount.getStatus(), release.getStatus());

        final String actualRequest = HttpTransportInterceptor.dequeueSentRequest().toString();
        assertTrue(actualRequest.contains("SELECT Name, Status__c FROM ADM_Release__c WHERE Id = '" + testAccount.getId() + "'"));
    }

    /**
     * Release ids that cannot be found should error
     */
    public void testQuery_NotFound() throws Exception {
        final NotFoundAccount notFoundTestRelease = new NotFoundAccount();

        HttpTransportInterceptor.enqueueMockResponses(notFoundTestRelease.getReleaseInfoQueryResponse());
        try {
            final Release release = new Release(notFoundTestRelease.getId());
            new ReleaseInfoQuerier(mockedGusConn).query(release);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Release not found", iae.getMessage());
        }
    }
}