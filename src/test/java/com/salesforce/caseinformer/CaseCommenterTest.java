package com.salesforce.caseinformer;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.transport.HttpTransportInterceptor;
import org.custommonkey.xmlunit.XMLTestCase;

import java.io.InputStream;

public class CaseCommenterTest extends XMLTestCase {

    private PartnerConnection mockedOrg62Conn;
    private TestAccount testAccount;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testAccount = new DefaultTestAccount();

        mockedOrg62Conn = new MockedConnectionLocator().getConnection();
        HttpTransportInterceptor.reset();
    }

    public void testComment() throws Exception {
        HttpTransportInterceptor.enqueueMockResponses(testAccount.getCommentSaveResponses());
        final Release release = testAccount.make();
        new CaseCommenter(mockedOrg62Conn).comment(release);

        for (InputStream req : testAccount.getCommentSaveRequests()) {
            assertXMLEqual(Resources.asString(req), HttpTransportInterceptor.dequeueSentRequest().toString());
        }

        for (Case c : release.getCases()) {
            assertNotNull(c.getCaseCommentSaveResult());
            assertTrue(c.getCaseCommentSaveResult().isSuccess());
            assertEquals(0, c.getCaseCommentSaveResult().getErrors().length);
        }
    }

    public void testComment_Validation() throws Exception {
        try {
            new CaseCommenter(mockedOrg62Conn).comment(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Release must not be null", e.getMessage());
        }
    }
}