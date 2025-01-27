package calculator;

import java.util.Arrays;
import java.util.regex.Pattern;

public class DefaultCalculator implements CalculationStrategy {
    public static final Pattern CUSTOM_SEPARATOR_PATTERN = Pattern.compile("//(.)\\\\n(.*)");
    public static final String SEPARATOR = "[,:]";

    // 이 클래스가 과연 필요한가?
    @Override
    public int calculate(String input) {
        String[] splitInput = input.split(SEPARATOR);
        return sum(splitInput);
    }

    @Override
    public boolean canCalculate(String input) {
        return !CUSTOM_SEPARATOR_PATTERN.matcher(input).find() && !input.isEmpty();
    }

    private int sum(String[] splitInput) {
        return Arrays.stream(splitInput)
                .mapToInt(this::convertToInt)
                .sum();
    }

    private int convertToInt(String a) {
        if (a.isEmpty()) {
            return 0;
        }
        try {
            return convertToPositiveNumber(a);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자의 형태가 아닙니다.", e);
        }

    }

    private int convertToPositiveNumber(String a) {
        int number = Integer.parseInt(a);
        if (number < 0) {
            throw new RuntimeException("음수는 들어올 수 없습니다.");
        }
        return number;
    }
}
