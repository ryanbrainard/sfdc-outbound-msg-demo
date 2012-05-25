package com.salesforce.caseinformer;

import com.google.common.collect.Lists;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import java.util.Collections;
import java.util.List;

public class ReleaseAnnouncer {

    private final List<ReleaseAnnouncementAction> actions;

    public ReleaseAnnouncer() throws ConnectionException {
        this(new GusConnectionLocator().getConnection(),
             new Org62ConnectionLocator().getConnection());
    }
    public ReleaseAnnouncer(final PartnerConnection gusConnection, final PartnerConnection org62Connection) throws ConnectionException {
        this(new ReleaseInfoQuerier(gusConnection),
             new ReleaseValidator(),
             new WorkInfoQuerier(gusConnection),
             new CaseCommenter(org62Connection));
    }

    public ReleaseAnnouncer(final ReleaseAnnouncementAction... actions) {
        this.actions = Collections.unmodifiableList(Lists.newArrayList(actions));
    }

    public Release announce(final String releaseId) throws ConnectionException {
        final Release release = new Release(releaseId);

        for (final ReleaseAnnouncementAction action : actions) {
            action.execute(release);
        }

        return release;
    }
}
