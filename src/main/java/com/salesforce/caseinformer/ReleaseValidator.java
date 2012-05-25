package com.salesforce.caseinformer;

import com.sforce.ws.ConnectionException;

public class ReleaseValidator implements ReleaseAnnouncementAction {

    @Override
    public void execute(Release release) throws ConnectionException {
        validateAnnounceability(release.getStatus());

    }

    void validateAnnounceability(Release.Status status) {
        if (status == null || !status.isAnnounceable()) {
            throw new IllegalArgumentException("Release is not in an announceable status. " +
                                               "Status is currently '" + status.getDisplay() + "'.");
        }
    }
}
