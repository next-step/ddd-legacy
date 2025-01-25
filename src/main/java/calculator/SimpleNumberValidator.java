package calculator;

public class SimpleNumberValidator implements NumberValidator {

    private static final String DIGIT_REGEX_PATTEN = "\\d+";


    @Override
    public void validateNumbers(String[] splittedString) {
        for (String str : splittedString) {
            if (!isDigit(str)) {
                throw new IllegalArgumentException("유효하지 않은 문자 : %s".formatted(str));
            }
        }
    }

    private boolean isDigit(String str) {
        return str.matches(DIGIT_REGEX_PATTEN);
    }

}
