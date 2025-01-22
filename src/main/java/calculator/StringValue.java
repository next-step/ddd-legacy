package calculator;

public class StringValue {
    private final String value;

    public StringValue(String value) {
        if (Integer.parseInt(value) < 0) {
            throw new RuntimeException("양의 정수를 입력하세요");
        }
        this.value = value;
    }
}
