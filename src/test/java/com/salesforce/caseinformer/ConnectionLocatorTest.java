/*
 * Copyright, 1999-2011, SALESFORCE.com 
 * All Rights Reserved
 * Company Confidential
 */
package com.salesforce.caseinformer;

import junit.framework.TestCase;

public class ConnectionLocatorTest extends TestCase {
    public void testGetProperty_TestOverride() throws Exception {
        assertEquals("false", new MockedConnectionLocator().getProperty(ConnectionLocator.PropertyKey.TRACE, true));
    }

}
