package calculator;

import java.util.List;
import java.util.stream.Stream;

public class PositiveIntegerExtractor {
    private final String originalValue;
    public PositiveIntegerExtractor(String originalValue) {
        this.originalValue = originalValue;
    }

    public static PositiveIntegerExtractor of(String originalValue, String defaultDelimiter, String customDelimiter) {
        validation(originalValue, defaultDelimiter + customDelimiter);
        return new PositiveIntegerExtractor(originalValue);
    }

    private static void validation(String originalValue, String delimiter) {
        if (!originalValue.matches("^[0-9]+([" + delimiter + "][0-9]+)*$")) {
            throw new RuntimeException("숫자 사이에 쉼표(,) 또는 콜론(:) 으로 구분된 문자열 형식이 아닙니다.");
        }
    }

    public List<PositiveInteger> getPositiveIntegers(String delimiter) {
        String[] strings = originalValue.split("[" + delimiter + "]");
        return Stream.of(strings)
                .map(PositiveInteger::of)
                .toList();
    }
}
