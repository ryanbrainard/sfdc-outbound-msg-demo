package com.salesforce.caseinformer;

import junit.framework.TestCase;

public class ReleaseValidatorTest extends TestCase {

    public void testValidateAnnounceability_Pass() throws Exception {
        new ReleaseValidator().validateAnnounceability(Release.Status.DEPLOYED_SUCCESSFULLY);
    }

    public void testValidateAnnounceability_Fail() throws Exception {
        try {
            new ReleaseValidator().validateAnnounceability(Release.Status.IN_DEVELOPMENT);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
