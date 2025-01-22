package calculator;

import java.util.StringTokenizer;

public class Calculator {

    private static final String DEFAULT_DELIMITER = ",:";

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
            String token = st.nextToken().trim();
            sum += Integer.parseInt(token);
        }
        return sum;
    }
}
