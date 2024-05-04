package calculator;

import java.util.Arrays;

class StringCalculator {

    private static final int INITIAL_RESULT = 0;
    private final StringSplitterMatcher splitterMatcher;

    StringCalculator() {
        this.splitterMatcher = new DefaultStringSplitterMatcher();
    }

    int add(final String separatedNumbers) {
        final var separatedString = new SeparatedString(splitterMatcher.match(separatedNumbers));
        if (separatedString.isEmpty()) {
            return INITIAL_RESULT;
        }
        final int[] intArray = parseToIntArray(separatedString.getValue());
        throwIfContainsNegative(intArray);
        return Arrays.stream(intArray).sum();
    }

    private int[] parseToIntArray(final String[] inputString) {
        try {
            return Arrays.stream(inputString).mapToInt(Integer::valueOf).toArray();
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 문자열이 포함되어있습니다.", e);
        }
    }

    private void throwIfContainsNegative(final int[] intArray) {
        if (Arrays.stream(intArray).anyMatch(v -> v < 0)) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
    }

    private static final class SeparatedString {

        private final String[] value;

        private SeparatedString(final StringSplitter stringSplitter) {
            this.value = stringSplitter.split();
        }

        private String[] getValue() {
            return this.value;
        }

        private boolean isEmpty() {
            return Arrays.stream(this.value).anyMatch(s -> s.length() == 0);
        }
    }
}