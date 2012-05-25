package com.salesforce.caseinformer;

import com.sforce.soap._2005._09.outbound.AccountNotification;
import com.sforce.soap.enterprise.sobject.Account;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.rmi.RemoteException;

@WebService
public class AccountNotificationHandler implements com.sforce.soap._2005._09.outbound.NotificationPort{

    private final static Logger logger = LoggerFactory.getLogger(AccountNotificationHandler.class);

    private final AccountAnnouncer announcer;

    @SuppressWarnings({"UnusedDeclaration"}) // called by Axis
    public AccountNotificationHandler() throws ConnectionException {
        this(new AccountAnnouncer());
    }

    AccountNotificationHandler(AccountAnnouncer announcer) {
        this.announcer = announcer;
    }

    @WebMethod
    @Override
    public boolean notifications(String organizationId, String actionId, String sessionId, String enterpriseUrl, String partnerUrl, AccountNotification[] notifications) throws RemoteException {
        logger.info("Received account notifications from org " + organizationId + " at " + partnerUrl);

        for (final AccountNotification n : notifications) {
            final Account account = n.getSObject();
            logger.info("Received account notification for " + account.getName() + " (" + account.getId() + ")");

            try {
                announcer.announce(account);
            } catch (ConnectionException e) {
                throwRemoteException(account, e);
            }
        }

        return true;
    }

    private void throwRemoteException(Account account, Exception e) throws RemoteException {
        final String msg = "Encountered " + e.getClass() + " for notification of Account: " + account.getName();
        logger.error(msg, e);
        throw new RemoteException(msg, e);
    }

}
