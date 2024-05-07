package calculator.exception;

public class InvalidNumberFormatException extends IllegalArgumentException {
    public static final String PARSING_INTEGER_EXCEPTION = "숫자로 변환할 수 없는 문자열 입니다.";

    public InvalidNumberFormatException(String value) {
        super(String.format(PARSING_INTEGER_EXCEPTION.concat(" : Invalid number = %s"), value));
    }
}
