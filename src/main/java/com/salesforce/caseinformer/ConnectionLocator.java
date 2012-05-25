package com.salesforce.caseinformer;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public abstract class ConnectionLocator {
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectionLocator.class);

    public static enum PropertyKey {
        USERNAME("username"),
        PASSWORD("password"),
        ENDPOINT("endpoint"),
        TRACE("trace"),
        TRIGGER_USER_EMAIL("triggerUserEmail");

        private String key;

        PropertyKey(String key) {
            this.key = key;
        }
    }

    private final String connectionKey;
    private final Properties properties;

    protected ConnectionLocator(final String connectionKey) {
        this.connectionKey = connectionKey;
        try {
            this.properties = Resources.asProperties("connections.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final PartnerConnection getConnection() throws ConnectionException {
        logger.info("Creating connection for: " + connectionKey);

        final ConnectorConfig config = getDefaultConfiguration();

        final boolean trace = Boolean.parseBoolean(getProperty(PropertyKey.TRACE, false));
		config.setTraceMessage(trace);
		config.setPrettyPrintXml(trace);

        final PartnerConnection connection = new PartnerConnection(config);

        connection.setEmailHeader(false, false, Boolean.parseBoolean(getProperty(PropertyKey.TRIGGER_USER_EMAIL, false)));

        return connection;
	}

    /**
     * Constructs the default configuration for this connection.
     * Subclasses should override if special configuration is needed.
     */
    protected ConnectorConfig getDefaultConfiguration() {
        final ConnectorConfig config = new ConnectorConfig();

        config.setUsername(getProperty(PropertyKey.USERNAME, true));
        config.setPassword(getProperty(PropertyKey.PASSWORD, true));
        config.setAuthEndpoint(getProperty(PropertyKey.ENDPOINT, true));
        return config;
    }

    protected final String getProperty(final PropertyKey propKey, final boolean isRequired) {
		final String propValue = properties.getProperty(connectionKey + "." + propKey.key);

		if (isRequired && propValue == null) {
			throw new IllegalArgumentException(propKey.key + " not set for " + connectionKey);
		}

		return propValue;
	}
}
