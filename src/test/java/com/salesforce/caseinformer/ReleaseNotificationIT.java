package com.salesforce.caseinformer;

import com.google.inject.internal.Preconditions;
import com.sforce.soap._2005._09.outbound.AccountNotification;
import com.sforce.soap._2005._09.outbound.NotificationPort;
import com.sforce.soap._2005._09.outbound.NotificationServiceLocator;
import com.sforce.soap.enterprise.sobject.Account;
import junit.framework.TestCase;
import org.apache.axis.AxisFault;

import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import java.net.URL;

public class ReleaseNotificationIT extends TestCase {

    private String gusEndpoint;

    protected void setUp() throws Exception {
        super.setUp();
        gusEndpoint = new GusConnectionLocator().getProperty(ConnectionLocator.PropertyKey.ENDPOINT, true);
        String org62Endpoint = new Org62ConnectionLocator().getProperty(ConnectionLocator.PropertyKey.ENDPOINT, true);
        String org62Username = new Org62ConnectionLocator().getProperty(ConnectionLocator.PropertyKey.USERNAME, true);
        Preconditions.checkArgument(!(org62Endpoint.contains("login") && org62Username.contains("salesforce.com")),
                        "Tests should not be hitting production!!!");

    }

    public void testNotificationWSDL() throws Exception {
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        URL url = new java.net.URL(new NotificationServiceLocator().getNotificationAddress() + "?WSDL");
        Service service = serviceFactory.createService(url, new NotificationServiceLocator().getServiceName());
        assertNotNull(service);
    }

    public void testNotification() throws Exception {
        final NotificationPort binding = new NotificationServiceLocator().getNotification();
        assertNotNull(binding);

        final String releaseId = "a0190000000Lzuv"; // 164.11.5 E-Release / Deployed Successfully
        final AccountNotification notification = new AccountNotification("", new Account(null, releaseId, ""));
        final AccountNotification[] notifications = {notification};

        final boolean notificationResult = binding.notifications("", "", "", "", gusEndpoint, notifications);
        assertTrue(notificationResult);
    }

    public void testNotificationWithReleaseInWrongStatus() throws Exception {
        final NotificationPort binding = new NotificationServiceLocator().getNotification();
        assertNotNull(binding);

        final String releaseId = "a01B0000000M3HdIAK"; // SFMA.91.2 (canceled) / 'Canceled or Never Released'
        final AccountNotification notification = new AccountNotification("", new Account(null, releaseId, ""));
        final AccountNotification[] notifications = {notification};

        try {
            binding.notifications("", "", "", "", gusEndpoint, notifications);
            fail();
        } catch (AxisFault e) {
            assertTrue(e.getMessage().contains(IllegalArgumentException.class.getName()));
        }
    }
}
