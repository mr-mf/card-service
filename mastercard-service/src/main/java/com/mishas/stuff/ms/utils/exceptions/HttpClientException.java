package com.mishas.stuff.ms.utils.exceptions;

public class HttpClientException extends RuntimeException {

    public HttpClientException() {
        super();
    }

    public HttpClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(final String message) {
        super(message);
    }

    public HttpClientException(final Throwable cause) {
        super(cause);
    }
}
