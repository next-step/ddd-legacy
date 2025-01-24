package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    public static final int DEFAULT_RESULT = 0;
    public static final String CUSTOM_REGEX = "//.\\n";
    static List<Integer> tokens;

    public static int calculate(final String operand) {
        validateNegativeNumber(operand);

        if (operand == null || operand.isEmpty()) {
            return DEFAULT_RESULT;
        }

        if (operand.chars().allMatch(Character::isDigit)) {
            return Integer.parseInt(operand);
        }
        Matcher matcher = Pattern.compile("//(.)\\n(.*)").matcher(operand);
        if (matcher.find()) {
            String customDelimeter = matcher.group(1);
            tokens = Arrays.stream(matcher.group(2).split(customDelimeter)).map(Integer::parseInt).toList();
            return tokens.stream().mapToInt(Integer::intValue).sum();
        }
        tokens = Arrays.stream(operand.split(":|,")).map(Integer::parseInt).toList();
        return tokens.stream().mapToInt(Integer::intValue).sum();
    }

    private static void validateNegativeNumber(String operand) {
        boolean isCustomOperator = Pattern.compile(CUSTOM_REGEX).matcher(operand).find();
        if (!isCustomOperator && operand.contains("-")) {
            throw new RuntimeException("음수는 입력할 수 없습니다");
        }
    }

}
