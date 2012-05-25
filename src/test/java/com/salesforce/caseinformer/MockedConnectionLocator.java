/*
 * Copyright, 1999-2011, SALESFORCE.com 
 * All Rights Reserved
 * Company Confidential
 */
package com.salesforce.caseinformer;

import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.transport.HttpTransportInterceptor;

public class MockedConnectionLocator extends ConnectionLocator {

    public MockedConnectionLocator() {
        super("mocked");
    }

    @Override
    protected ConnectorConfig getDefaultConfiguration() {
        final ConnectorConfig mockedConfig = new ConnectorConfig();

        mockedConfig.setServiceEndpoint("https://login.salesforce.com/services/Soap/u/MOCKED");
        mockedConfig.setTransport(HttpTransportInterceptor.class);
        mockedConfig.setManualLogin(true);

        return mockedConfig;
    }
}
