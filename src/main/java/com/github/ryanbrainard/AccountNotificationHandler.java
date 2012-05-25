package com.github.ryanbrainard;

import com.sforce.soap._2005._09.outbound.AccountNotification;
import com.sforce.soap.enterprise.sobject.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.rmi.RemoteException;

@WebService
public class AccountNotificationHandler implements com.sforce.soap._2005._09.outbound.NotificationPort {

    private final static Logger logger = LoggerFactory.getLogger(AccountNotificationHandler.class);

    @WebMethod
    @Override
    public boolean notifications(String organizationId, String actionId, String sessionId, String enterpriseUrl, String partnerUrl, AccountNotification[] notifications) throws RemoteException {
        logger.info("Received account notifications from org " + organizationId + " at " + partnerUrl);

        for (final AccountNotification n : notifications) {
            final Account account = n.getSObject();
            logger.info("Received account notification for " + account.getName() + " (" + account.getId() + ")");
        }

        return true;
    }
}
