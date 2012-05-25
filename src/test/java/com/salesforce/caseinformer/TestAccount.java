package com.salesforce.caseinformer;

import com.sforce.soap._2005._09.outbound.AccountNotification;

import java.io.FileNotFoundException;
import java.io.InputStream;

public abstract class TestAccount {

    public String getId() {
        return "RELEASE_ID_0001";
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public Release.Status getStatus() {
        throw new UnsupportedOperationException();
    }

    public AccountNotification getNotification() {
        throw new UnsupportedOperationException();
    }

    public abstract InputStream getReleaseInfoQueryResponse() throws FileNotFoundException;

    public InputStream[] getWorkInfoQueryResponses() throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    public InputStream[] getCommentSaveRequests() throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    public InputStream[] getCommentSaveResponses() throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    public Release make() {
        throw new UnsupportedOperationException();
    }
}
