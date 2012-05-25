package com.salesforce.caseinformer;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Resources {

    private Resources() {}

    static InputStream asStream(String resourceName) throws FileNotFoundException {
        final InputStream resourceAsStream = Resources.class.getResourceAsStream(resourceName);

        if (resourceAsStream == null) {
            throw new FileNotFoundException("Resource " + resourceName + " could not be found");
        }

        return resourceAsStream;
    }

    static Properties asProperties(final String propertyFileName) throws IOException {
        final InputStream propStream = asStream(propertyFileName);
        final Properties props = new Properties();

        props.load(propStream);

        return props;
    }

    static String asString(final String resourceName) throws IOException {
        return asString(asStream(resourceName));
    }

    static String asString(InputStream resourceAsStream) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(resourceAsStream);

        StringBuffer stringBuffer = new StringBuffer();
        int position = 0;
        byte[] buffer = new byte[1024];
        while ((position = in.read(buffer, 0, buffer.length)) > -1) {
            stringBuffer.append(new String(buffer, 0, position));
        }
        in.close();

        return stringBuffer.toString();
    }
}
