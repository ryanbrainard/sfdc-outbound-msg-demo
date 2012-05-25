package com.salesforce.caseinformer;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.sforce.soap.partner.SaveResult;

import java.util.Collection;
import java.util.Collections;

public final class Case {

	private final String id;
    private final String number;
    private final String subject;
    private final String accountName;
    private final String owner;
    private final Collection<Work> workItems = Sets.newHashSet();
    private SaveResult caseCommentSaveResult;

    public Case(String id, String number, String subject, String accountName, String owner) {
        this.id = Ids.validate(id);
        this.number = number;
        this.subject = subject;
        this.accountName = accountName;
        this.owner = owner;
    }

    public Collection<Work> getWorkItems() {
		return Collections.unmodifiableCollection(workItems);
	}

	public void addWorkItem(Work work) {
		workItems.add(work);
	}

	public void setCaseCommentSaveResult(SaveResult caseCommentSaveResult) {
		this.caseCommentSaveResult = caseCommentSaveResult;
	}

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getSubject() {
        return subject;
    }

    public String getOwner() {
        return owner;
    }

	public SaveResult getCaseCommentSaveResult() {
		return caseCommentSaveResult;
	}

    /**
     * Only uses case id for hash code for performance reasons, since this will almost always be unique.
     * Equals performs a full deep comparison.
     */
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public boolean equals(Object object) {
	  if (object instanceof Case) {
		  Case that = (Case) object;
		  return Objects.equal(this.id, that.id)
		         && Objects.equal(this.number, that.number)
		         && Objects.equal(this.subject, that.subject)
		         && Objects.equal(this.accountName, that.accountName)
		         && Objects.equal(this.owner, that.owner)
		         && Objects.equal(this.workItems, that.workItems)
		         && Objects.equal(this.caseCommentSaveResult, that.caseCommentSaveResult);
	  }
	  
	  return false;
	}

    @Override
    public String toString() {
        return "Case{" +
               "id='" + id + '\'' +
               ", number='" + number + '\'' +
               ", subject='" + subject + '\'' +
               ", accountName='" + accountName + '\'' +
               ", owner='" + owner + '\'' +
               ", workItems=" + workItems +
               ", caseCommentSaveResult=" + caseCommentSaveResult +
               '}';
    }
}
