package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_SEPERATORS = ",|:";
    private static final String DELIMITER_REGEX = "//(.)\\n(.*)";

    public int add(String input) {
        if (input == null || input.isEmpty()) return 0;
        try {
            String[] divided = initSeparator(input);

            String separator = divided[0];
            String inputString = divided[1];

            List<String> tokens = tokenize(inputString, separator);
            List<Integer> numbers = parse(tokens);

            return sum(numbers);

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public String[] initSeparator(String input) {
        Matcher matcher = Pattern.compile(DELIMITER_REGEX).matcher(input);

        String customSeperator = DEFAULT_SEPERATORS;
        String inputString = input;

        if (matcher.find()) {
            customSeperator = matcher.group(1);
            inputString = matcher.group(2);
        }

        return new String[]{customSeperator, inputString};
    }

    private List<String> tokenize(String input, String seperator) {
        return Arrays.stream(input.split(seperator)).toList();
    }

    private List<Integer> parse(List<String> tokens) {
        return tokens.stream().map((String v) -> {
            int n = Integer.parseInt(v);
            if (n < 1) throw new RuntimeException("음수는 계산할 수 없습니다.");
            return n;
        }).toList();
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream().reduce(0, Integer::sum);
    }

}
