package com.salesforce.caseinformer;

import com.sforce.soap._2005._09.outbound.AccountNotification;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class AccountNotificationHandler implements com.sforce.soap._2005._09.outbound.NotificationPort{

    private final static Logger logger = LoggerFactory.getLogger(AccountNotificationHandler.class);

    private final ReleaseAnnouncer announcer;

    @SuppressWarnings({"UnusedDeclaration"}) // called by Axis
    public AccountNotificationHandler() throws ConnectionException {
        this(new ReleaseAnnouncer());
    }

    AccountNotificationHandler(ReleaseAnnouncer announcer) {
        this.announcer = announcer;
    }

    @Override
    public boolean notifications(String organizationId, String actionId, String sessionId, String enterpriseUrl, String partnerUrl, AccountNotification[] notification) throws RemoteException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
