package calculator;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator{

    private final static int ZERO = 0;
    private final static String ALPHABET_PATTERN = "[a-zA-Z]";
    private final static Pattern DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");


    public static int calculate(String input) {
        if (isBlank(input)) {
            return ZERO;
        }
        validate(input);
        return sum(toInts(split(input)));
    }

    private static void validate(String input) {
        if (hasNonNumber(input)) {
            throw new RuntimeException('"' + input + "\" is not a valid number string");
        }
    }

    private static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }

    private static boolean hasNonNumber(String input) {
        return input.matches(ALPHABET_PATTERN);
    }


    public static int sum(List<Integer> numbers) {
        return numbers.stream().mapToInt(it -> it).sum();
    }

    private static String[] split(String input) {
        Matcher m = DELIMITER_PATTERN.matcher(input);
        if(!m.find()){
            return input.split("[,:]");
        }

        String customDelimiter = m.group(1);
        return m.group(2).split(customDelimiter);
    }

    private static List<Integer> toInts(String[] values) {
        return Arrays.stream(values)
                .map(Calculator::toInt)
                .toList();
    }

    private static int toInt(String values) {
        int value = Integer.parseInt(values);
        if (value < 0) {
            throw new RuntimeException();
        }
        return value;
    }
}
