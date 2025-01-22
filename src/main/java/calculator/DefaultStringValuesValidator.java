package calculator;

public class DefaultStringValuesValidator implements StringValuesValidator {

    @Override
    public void validation(String value, String defaultDelimiterRegex) {
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("빈 문자열이 입력되었습니다.");
        }
        if (!value.matches("^[0-9]+(" + defaultDelimiterRegex + "[0-9]+)*$")) {
            throw new RuntimeException("숫자 사이에 쉼표(,) 또는 콜론(:) 으로 구분된 문자열 형식이 아닙니다.");
        }
    }
}
