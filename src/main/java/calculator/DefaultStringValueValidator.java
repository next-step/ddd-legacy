package calculator;

public class DefaultStringValueValidator implements StringValueValidator {

    @Override
    public void validation(String value) {
        int intValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("양의 정수를 입력하세요");
        }

        if (intValue < 0) {
            throw new RuntimeException("양의 정수를 입력하세요");
        }
    }
}
