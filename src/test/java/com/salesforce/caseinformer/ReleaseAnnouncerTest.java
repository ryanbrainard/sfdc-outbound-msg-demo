package com.salesforce.caseinformer;

import junit.framework.TestCase;

import static org.mockito.Mockito.*;

public class ReleaseAnnouncerTest extends TestCase {
    private TestAccount testAccount;
    private ReleaseAnnouncementAction actionOne;
    private ReleaseAnnouncementAction actionTwo;
    private ReleaseAnnouncer announcer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testAccount = new DefaultTestAccount();

        actionOne = mock(ReleaseAnnouncementAction.class);
        actionTwo = mock(ReleaseAnnouncementAction.class);
        announcer = new ReleaseAnnouncer(actionOne, actionTwo);
    }

    public void testAnnounce() throws Exception {
        final String releaseId = testAccount.getId();
        final Release release = new Release(releaseId);

        assertEquals(release, announcer.announce(releaseId));

        verify(actionOne).execute(release);
        verify(actionTwo).execute(release);
    }
}
