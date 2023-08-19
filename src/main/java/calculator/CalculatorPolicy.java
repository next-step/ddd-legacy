package calculator;

public interface CalculatorPolicy {

    String NOT_NEGATIVE_MESSAGE = "음수는 입력불가합니다.";
    int ZERO = 0;

    boolean isSupport(String text);

    int calculate(String text);

    default int toPositive(String text) {
        int value = Integer.parseInt(text);
        if (isNegative(value)) {
            throw new RuntimeException(NOT_NEGATIVE_MESSAGE);
        }

        return value;
    }

    private boolean isNegative(int value) {
        return value < ZERO;
    }

}
