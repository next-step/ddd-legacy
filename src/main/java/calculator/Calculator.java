package calculator;

import java.util.StringTokenizer;

public class Calculator {

    private static final String DEFAULT_DELIMITER = ",:";
    private static final String NOT_NUMBER_EXCEPTION = "숫자 외의 값을 넣을 수 없습니다.";
    private static final String NEGATIVE_NUMBER_EXCEPTION = "음수를 넣을 수 없습니다.";

    public int calculate(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        if (value.matches("[\"']+")) {
            return 0;
        }

        String delimiter = parseDelimiter(value);
        String targetValue = parseTarget(value, delimiter);

        return calculateSum(targetValue, delimiter);
    }

    private String parseDelimiter(String value) {
        if (value.startsWith("//")) {
            int newLineIndex = value.indexOf("\\n");
            if (newLineIndex != -1) {
                return value.substring(2, newLineIndex);
            }
        }
        return DEFAULT_DELIMITER;
    }

    private String parseTarget(String value, String delimiter) {
        if (delimiter.equals(DEFAULT_DELIMITER)) {
            return value;
        }
        return value.substring(value.indexOf(delimiter) + 3);
    }

    private int calculateSum(String numberString, String delimiter) {
        int sum = 0;
        StringTokenizer st = new StringTokenizer(numberString, delimiter);
        while (st.hasMoreElements()) {
            try {
                int targetNumber = Integer.parseInt(st.nextToken().trim());
                validateNegativeNumber(targetNumber);
                sum += targetNumber;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(NOT_NUMBER_EXCEPTION);
            }
        }
        return sum;
    }

    private void validateNegativeNumber(int targetNumber) {
        if (targetNumber < 0) {
            throw new IllegalArgumentException(NEGATIVE_NUMBER_EXCEPTION);
        }
    }
}
