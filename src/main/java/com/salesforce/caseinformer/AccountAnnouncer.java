package com.salesforce.caseinformer;

import com.google.common.collect.Lists;
import com.sforce.soap.enterprise.sobject.Account;
import com.sforce.ws.ConnectionException;

import java.util.Collections;
import java.util.List;

public class AccountAnnouncer {

    private final List<AccountAnnouncementAction> actions;

    public AccountAnnouncer(final AccountAnnouncementAction... actions) {
        this.actions = Collections.unmodifiableList(Lists.newArrayList(actions));
    }

    public void announce(final Account account) throws ConnectionException {
        for (final AccountAnnouncementAction action : actions) {
            action.execute(account);
        }
    }
}
