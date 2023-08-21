package calculator;

public class NotPositiveNumberException extends RuntimeException {
    public NotPositiveNumberException() {
    }

    public NotPositiveNumberException(final String message) {
        super(message);
    }

    public NotPositiveNumberException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NotPositiveNumberException(final Throwable cause) {
        super(cause);
    }

    public NotPositiveNumberException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
