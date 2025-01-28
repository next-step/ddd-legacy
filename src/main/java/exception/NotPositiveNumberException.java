package exception;

import static exception.ExceptionDetails.NOT_POSITIVE_NUMBER_EXCEPTION;

public class NotPositiveNumberException extends RuntimeException {

    public NotPositiveNumberException() {
        super(NOT_POSITIVE_NUMBER_EXCEPTION.getMessage());
    }

}
