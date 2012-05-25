package com.salesforce.caseinformer;

import com.google.common.base.Objects;

public final class Work {
	private String number;
	private String subject;

    public Work(final String number, final String subject) {
        this.number = number;
        this.subject = subject;
    }

	public String getNumber() {
		return number;
	}

	public String getSubject() {
		return subject;
	}

    /**
     * Only uses work number for hash code for performance reasons, since this will almost always be unique.
     * Equals performs a full deep comparison.
     */
	@Override
	public int hashCode() {
		return Objects.hashCode(number);
	}

	@Override
	public boolean equals(Object object) {
	  if (object instanceof Work) {
		  Work that = (Work) object;
		  return Objects.equal(this.number, that.number)
		         && Objects.equal(this.subject, that.subject);
	  }
	  
	  return false;
	}

    @Override
    public String toString() {
        return "Work{" +
               "number='" + number + '\'' +
               ", subject='" + subject + '\'' +
               '}';
    }
}
