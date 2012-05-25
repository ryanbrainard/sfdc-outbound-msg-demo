package com.salesforce.caseinformer;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class NotFoundAccount extends TestAccount {

    @Override
    public InputStream getReleaseInfoQueryResponse() throws FileNotFoundException {
        return Resources.asStream("gusReleaseInfoQueryResult_NotFound.xml");
    }

}