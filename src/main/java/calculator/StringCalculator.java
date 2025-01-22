package calculator;

public class StringCalculator {
    private final String value;

    public StringCalculator(String value) {
        if (Integer.parseInt(value) < 0) {
            throw new RuntimeException("양의 정수를 입력하세요");
        }
        this.value = value;
    }
}
