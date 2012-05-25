package com.salesforce.caseinformer;

import com.sforce.soap.enterprise.sobject.Account;
import com.sforce.ws.ConnectionException;

/**
 * Performs an action when announcing a given Account
 */
public interface AccountAnnouncementAction {

	void execute(final Account account) throws ConnectionException;
	
}
