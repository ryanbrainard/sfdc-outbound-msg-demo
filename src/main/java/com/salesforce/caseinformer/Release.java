package com.salesforce.caseinformer;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class Release {

    private final String id;
    private String name;
    private Status status;
    private final Map<String, Case> casesById = Maps.newHashMap();


    @SuppressWarnings({"UnusedDeclaration"})
    static enum Status {
        DEPLOYED_OTHER_ISSUES("Deployed - Other Issues", false), //TODO: is this announceable
        DEPLOYED_SUCCESSFULLY("Deployed Successfully", true),
        DEPLOYMENT_IN_PROGRESS("Deployment in Progress", false),
        DEPLOYMENT_INTRODUCED_REGRESSION("Deployment Introduced Regression", false), //TODO: is this announceable
        IN_DEVELOPMENT("In Development", false),
        PARTIAL_DEPLOYMENT_HALTED("Partial Deployment - Halted", false),
        PARTIAL_DEPLOYMENT_SUPERSEDED("Partial Deployment - Superseded", false),
        PARTIAL_ROLLBACK("Partial Rollback", false),
        READY_FOR_DEPLOYMENT("Ready for Deployment", false),
        REDEPLOYED_AFTER_ROLLBACK("Redeployed After Rollback", false), //TODO: is this announceable
        ROLLBACK("Rollback", false),
        DEPLOYED_EXCEEDED_RELEASE_WINDOW("Deployed - Exceeded Release Window", true), //TODO: is this announceable
        DEPLOYED_DATE_CHANGED("Deployed - Date Changed", false),
        CANCELED_OR_NEVER_RELEASED("Canceled or Never Released", false);

        private final String apiValue;
        private final boolean isAnnounceable;

        Status(String apiValue, boolean isValid) {
            this.apiValue = apiValue;
            this.isAnnounceable = isValid;
        }

        static Status fromApiValue(String apiValue) {
            for (Status e : values()) {
                if (e.apiValue.equals(apiValue)) {
                    return e;
                }
            }

            throw new IllegalArgumentException("Unknown Status: " + apiValue);
        }

        public boolean isAnnounceable() {
            return isAnnounceable;
        }

        public String getApiValue() {
            return apiValue;
        }

        public String getDisplay() {
            return getApiValue();
        }
    }
    public Release(String id) {
        this.id = Ids.validate(id);
    }

    public void addCase(Case c) {
        if (c == null) {
            throw new IllegalArgumentException("Case must not be null");
        }

        casesById.put(c.getId(), c);
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStatus(String status) {
        this.status = Status.fromApiValue(status);
    }

    public Status getStatus() {
        return status;
    }

    public boolean hasCase(String caseId) {
        return casesById.containsKey(caseId);
    }

    public Case getCase(String caseId) {
        if (!hasCase(caseId)) {
            throw new IllegalArgumentException("No such case associated with this release.");
        }

        return casesById.get(caseId);
    }

    public Collection<Case> getCases() {
        return Collections.unmodifiableCollection(casesById.values());
    }

    /**
     * Only uses release id for hash code for performance reasons, since this will always be unique.
     * Equals performs a full deep comparison.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Release) {
            Release that = (Release) object;
            return Objects.equal(this.id, that.id)
                    &&Objects.equal(this.name, that.name)
                    &&Objects.equal(this.status, that.status)
                    && Objects.equal(this.casesById, that.casesById);
        }

        return false;
    }

    @Override
    public String toString() {
        return "Release{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "status='" + status + '\'' +
                ", casesById=" + casesById +
                '}';
    }
}
