package calculator;

import java.util.Arrays;

public record NumberParser() {

    public int[] parse(String[] tokens) {
        return Arrays.stream(tokens)
            .map(String::trim)
            .filter(token -> !token.isEmpty())
            .mapToInt(Integer::parseInt)
            .toArray();
    }
}
