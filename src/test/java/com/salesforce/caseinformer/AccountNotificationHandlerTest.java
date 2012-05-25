package com.salesforce.caseinformer;

import com.google.common.base.Preconditions;
import com.sforce.soap._2005._09.outbound.AccountNotification;
import com.sforce.ws.ConnectionException;
import junit.framework.TestCase;

import java.rmi.RemoteException;

import static org.mockito.Mockito.*;

public class AccountNotificationHandlerTest extends TestCase {

    private ReleaseAnnouncer announcer;
    private AccountNotificationHandler handler;
    private String configuredGusEndpoint;
    private TestAccount testAccount;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        announcer = mock(ReleaseAnnouncer.class);
        handler = new AccountNotificationHandler(announcer);
        configuredGusEndpoint = new GusConnectionLocator().getProperty(ConnectionLocator.PropertyKey.ENDPOINT, true);
        testAccount = new DefaultTestAccount();
    }

    public void testNotifications_Single() throws Exception {
        final AccountNotification[] notifications = {testAccount.getNotification()};

        handler.notifications("", "", "", "", configuredGusEndpoint, notifications);

        verify(announcer, times(notifications.length)).announce(anyString());
        verify(announcer).announce(testAccount.getId());
    }

    public void testNotifications_Bulk() throws Exception {
        final AccountNotification[] notifications = {testAccount.getNotification(), testAccount.getNotification()};

        handler.notifications("", "", "", "", configuredGusEndpoint, notifications);

        verify(announcer, times(notifications.length)).announce(anyString());
        verify(announcer, times(2)).announce(testAccount.getId());
    }

    public void testNotifications_GusEndpointMisconfiguration() throws Exception {
        final AccountNotification[] notifications = {testAccount.getNotification()};

        final String wrongEndpoint = "blah";
        Preconditions.checkArgument(!configuredGusEndpoint.equals(wrongEndpoint));

        try {
            handler.notifications("", "", "", "", wrongEndpoint, notifications);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Received notification from '" + wrongEndpoint + "', but GUS connection is configured for " + configuredGusEndpoint, e.getMessage());
        }

        verify(announcer, never()).announce(anyString());
    }

    public void testNotifications_ThrowsRemoteException() throws Exception {
        final AccountNotification[] notifications = {testAccount.getNotification()};
        final Exception wrappedException = new ConnectionException();
        doThrow(wrappedException).when(announcer).announce(anyString());

        try {
            handler.notifications("", "", "", "", configuredGusEndpoint, notifications);
            fail();
        } catch (RemoteException re) {
            assertEquals(wrappedException, re.getCause());
        }

        verify(announcer).announce(anyString());
    }
}
