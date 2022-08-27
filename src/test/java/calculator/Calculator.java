package calculator;

import java.util.Arrays;
import java.util.Objects;

public class Calculator {

    public static final int DEFAULT_VALUE = 0;

    public static int sum(final String input) {
        if (Objects.isNull(input) || input.isBlank()) {
            return DEFAULT_VALUE;
        }

        String[] numbers = input.split(",|:");
        return Arrays.stream(numbers)
                .mapToInt(Calculator::parseInt)
                .sum();
    }

    private static int parseInt(String value){
        checkNumber(value);
        checkNegative(value);
        return Integer.parseInt(value);
    }

    private static void checkNegative(String value) {
        int iValue = Integer.parseInt(value);

        if(iValue < 0) {
            throw new RuntimeException("문자열 계산기에서 음수값은 허용되지 않습니다.");
        }
    }

    private static void checkNumber(String value) {
        boolean isNumber = value.matches("\\d");

        if(!isNumber) {
            throw new RuntimeException("숫자 이외의 값은 허용되지 않습니다.");
        }
    }
}
