package com.sforce.ws.transport;



import java.io.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Intercepts requests and responses to allow tests to capture requests sent and provide mocked responses.
 * This is helpful for (1) inspecting the serialized contents of a given request that would have been sent
 * to the endpoint, and (2) providing serialized mocked responses to be deserialized into Java objects to
 * eliminate the need to manually create and nest objects to simulate a deserialized response.
 *
 * All requests sent to this transport are captured in a thread local queue and not actually sent to the configured endpoint.
 * These requests can later be dequeued by calling dequeueSentRequest().
 *
 * All responses expected to be returned from this transport must be first mocked and enqueued by calling enqueueMockResponse().
 * These responses are placed on a thread local queue and later dequeued when the connection attempts to receive a response.
 */
public class HttpTransportInterceptor extends JdkHttpTransport {
	
	private static ThreadLocal<Queue<OutputStream>> sentRequests = new ThreadLocal<Queue<OutputStream>>() {
            @Override
            protected Queue<OutputStream> initialValue() {
                return new LinkedList<OutputStream>();
            }
        };
	
	private static ThreadLocal<Queue<InputStream>> mockResponses = new ThreadLocal<Queue<InputStream>>() {
            @Override
            protected Queue<InputStream> initialValue() {
                return new LinkedList<InputStream>();
            }
        };

    /**
     * Resets this transport by clearing all requests and responses for this thread.
     */
	public static void reset() {
		mockResponses.get().clear();
        sentRequests.get().clear();
	}

    /**
     * Enqueues InputStreams for this thread to be later returned as a mocked response
     * when the connection attempts to getContent() from this transport in the order provided.
     *
     * @param mockResponsesToEnqueue
     */
    public static void enqueueMockResponses(final InputStream... mockResponsesToEnqueue) {
		for (InputStream r : mockResponsesToEnqueue) {
            mockResponses.get().offer(r);
        }
	}

    /**
     * Provides the next sent request as an OutputStream.
     * This transport must be used prior to a expecting a request on the queue.
     *
     * @return next sent request
     * @throws IllegalStateException if no enqueued request exists
     */
    public static OutputStream dequeueSentRequest() throws NoSuchElementException {
        if (sentRequests.get().isEmpty()) {
            throw new IllegalStateException("Must use this transport prior to expecting a sent request.");
        }

        return sentRequests.get().remove();
    }

    /**
     * Provides the connection the next mock response from the queue.
     * Only to be called by the connection -- not by tests!
     *
     * @return the next mock response from the queue
     * @throws IllegalStateException if no responses are enqueued
     */
    @Override
	public InputStream getContent() throws IllegalStateException {
        if (mockResponses.get().isEmpty()) {
            throw new IllegalStateException("Must enqueue a mock response prior to using this transport.");
        }

		return mockResponses.get().remove();
	}

    /**
     * Enqueues and returns an empty OutputStream that the connection will later populate.
     * Only to be called by the connection -- not by tests!
     */
	@Override
	public OutputStream connect(String uri, String soapAction) {
		OutputStream out = new ByteArrayOutputStream();
		sentRequests.get().offer(out);
		return out;
	}

    /**
     * Intercepted transport is always successful.
     */
	@Override
	public boolean isSuccessful() {
		return true;
	}
}
