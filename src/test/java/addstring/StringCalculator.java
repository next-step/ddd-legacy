package addstring;

import java.util.Arrays;

public class StringCalculator {

    private static final String WRAPPED_STRING = "//\n";
    private static final String DEFAULT_SEPARATOR = ",:";

    private String separator;

    public StringCalculator() {
        this.separator = DEFAULT_SEPARATOR;
    }

    public int add(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        String stringToCalculate = checkPolicy(s);

        String[] stringNumberArray = splitStringToArrayBySeparator(stringToCalculate, this.separator);
        checkNegativeNumber(stringNumberArray);

        return Arrays.stream(stringNumberArray)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private String checkPolicy(String s) {
        String[] parsedString = splitStringToArrayBySeparator(s, WRAPPED_STRING);

        if (parsedString[0].equals(s)) {
            return s;
        }

        int lastIndex = parsedString.length - 1;
        String customSeparator = parsedString[lastIndex - 1];
        this.separator = this.separator.concat(customSeparator);

        String stringToCalculate = parsedString[lastIndex];
        return stringToCalculate;
    }

    private void checkNegativeNumber(String[] stringNumberArray) {
        boolean hasNegativeNumber = Arrays.stream(stringNumberArray)
                .mapToInt(Integer::parseInt)
                .anyMatch(s -> s < 0);

        if (hasNegativeNumber) {
            throw new RuntimeException("음수를 입력할 수 없습니다.");
        }
    }

    private String[] splitStringToArrayBySeparator(String s, String separator) {
        String separatorPolicy = String.format("[%s]", separator);
        return s.split(separatorPolicy);
    }

}
