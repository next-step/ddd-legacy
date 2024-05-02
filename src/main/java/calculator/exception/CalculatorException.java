package calculator.exception;

import global.exception.BusinessException;

public class CalculatorException extends BusinessException {

    public CalculatorException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }

}
