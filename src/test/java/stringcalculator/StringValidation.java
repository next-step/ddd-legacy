package stringcalculator;

enum ValidationNumber {

    NEGATIVE_NUM_REGEX("^-\\d+$"),
    POSITIVE_NUM_REGEX("^\\d+$");

    private final String regex;

    ValidationNumber(String regex) {
        this.regex = regex;
    }
    public String getRegex() {
        return regex;
    }

}

public class StringValidation {

    // 유효성 검증 정규식을 ENUM 으로 분리
    private final String negativeValid = ValidationNumber.NEGATIVE_NUM_REGEX.getRegex();

    public boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public void checkNegative(String[] numbers) {
        for (String number : numbers) {
            if(number.matches(negativeValid)) {
                throw new RuntimeException();
            }
        }
    }
}
