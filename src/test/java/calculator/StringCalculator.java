package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.util.Pair;

class StringCalculator {

    private static final int INITIAL_RESULT = 0;
    private static final String CUSTOM_INPUT_REGEX = "//(.)\\n(.*)";
    private static final String CUSTOM_INPUT_SEPARATOR_FORMAT = "[,|:|%s]";
    private static final String DEFAULT_SEPARATOR_FORMAT = "[,|:]";

    int add(final String separatedNumbers) {
        if (separatedNumbers == null || separatedNumbers.isEmpty()) {
            return INITIAL_RESULT;
        }
        final Pair<String, String> data = convert(separatedNumbers);
        return data.getFirst().isEmpty() ? INITIAL_RESULT : add(data.getFirst(), data.getSecond());
    }

    private Pair<String, String> convert(final String s) {
        final Pattern pattern = Pattern.compile(CUSTOM_INPUT_REGEX);
        final Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            final String separator = String.format(CUSTOM_INPUT_SEPARATOR_FORMAT, matcher.group(1));
            return Pair.of(matcher.group(2), separator);
        }
        return Pair.of(s, DEFAULT_SEPARATOR_FORMAT);
    }

    private int add(final String inputString, final String separator) {
        final int[] intArray = parseToIntArray(inputString, separator);
        throwIfContainsNegative(intArray);
        return Arrays.stream(intArray).sum();
    }

    private int[] parseToIntArray(String inputString, String separator) {
        try {
            return Arrays.stream(inputString.split(separator)).mapToInt(Integer::valueOf).toArray();
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 문자열이 포함되어있습니다.", e);
        }
    }

    private void throwIfContainsNegative(int[] intArray) {
        if (Arrays.stream(intArray).anyMatch(v -> v < 0)) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
    }
}