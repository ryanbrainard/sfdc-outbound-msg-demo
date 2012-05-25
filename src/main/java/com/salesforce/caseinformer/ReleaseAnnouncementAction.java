package com.salesforce.caseinformer;

import com.sforce.ws.ConnectionException;

/**
 * Performs an action when announcing a given Release
 */
public interface ReleaseAnnouncementAction {

	void execute(final Release release) throws ConnectionException;
	
}
