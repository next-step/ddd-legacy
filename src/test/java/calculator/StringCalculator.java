package calculator;

import java.util.Arrays;

class StringCalculator {

    private static final int INITIAL_RESULT = 0;
    private final StringSplitterFactory stringSplitterFactory;

    public StringCalculator() {
        this.stringSplitterFactory = new DefaultStringSplitterFactory();
    }

    int add(final String source) {
        final var parsedSource = SeparatedString.parseSeparatedString(source,
            stringSplitterFactory);
        if (parsedSource.isEmpty()) {
            return INITIAL_RESULT;
        }
        final var parsedNumbers = PositiveNumbers.parsePositiveNumbers(parsedSource.value());
        return Arrays.stream(parsedNumbers.value()).sum();
    }

    private record SeparatedString(String[] value) {

        private static SeparatedString parseSeparatedString(final String source,
            final StringSplitterFactory stringSplitterFactory) {
            final StringSplitter matchedSplitter = stringSplitterFactory.create(source);
            return new SeparatedString(matchedSplitter.split(source));
        }

        private boolean isEmpty() {
            return Arrays.stream(this.value).allMatch(s -> s.length() == 0);
        }
    }

    private record PositiveNumbers(int[] value) {

        PositiveNumbers {
            throwIfContainsNegative(value);
        }

        static PositiveNumbers parsePositiveNumbers(final String[] valueAsString) {
            return new PositiveNumbers(convertToInts(valueAsString));
        }

        private static int[] convertToInts(final String[] valueAsString) {
            try {
                return Arrays.stream(valueAsString).mapToInt(Integer::valueOf).toArray();
            } catch (NumberFormatException e) {
                throw new RuntimeException("숫자가 아닌 문자열이 포함되어있습니다.", e);
            }
        }

        private void throwIfContainsNegative(final int[] intArray) {
            if (Arrays.stream(intArray).anyMatch(v -> v < 0)) {
                throw new RuntimeException("음수는 입력할 수 없습니다.");
            }
        }
    }
}