package exception;

import static exception.ExceptionDetails.INVALID_NUMBER_FORMAT_EXCEPTION;

public class InvalidNumberFormatException extends RuntimeException {

    public InvalidNumberFormatException() {
        super(INVALID_NUMBER_FORMAT_EXCEPTION.getMessage());
    }

}
