package com.salesforce.caseinformer;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.transport.HttpTransportInterceptor;
import junit.framework.TestCase;

public class WorkInfoQuerierTest extends TestCase {

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
     * Queries GUS for a given release, which is associated with two work items and two cases;
     * however, one of the cases is associated with both of the work items. The querier should
     * correctly parse the query result to only list the cases once, but list the work items
     * under each of its associated cases. Also, tests that queryMores are handled correctly.
     */
    public void testQuery() throws Exception {
        HttpTransportInterceptor.enqueueMockResponses(testAccount.getWorkInfoQueryResponses());

        final Release release = new Release(testAccount.getId());
        release.setName(testAccount.getName());
        new WorkInfoQuerier(mockedGusConn).query(release);
        assertEquals(testAccount.make(), release);
    }
}