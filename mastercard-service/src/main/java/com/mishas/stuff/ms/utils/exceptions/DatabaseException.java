package com.mishas.stuff.ms.utils.exceptions;

public final class DatabaseException extends RuntimeException {

    public DatabaseException() {
        super();
    }

    public DatabaseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(final String message) {
        super(message);
    }

    public DatabaseException(final Throwable cause) {
        super(cause);
    }
}
