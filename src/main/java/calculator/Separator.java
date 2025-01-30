package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Separator {

    private final List<String> separators = new ArrayList<>(Arrays.asList(",", ":"));

    public Stream<PositiveNumber> separate(String input) {
        if (input.startsWith("//")) {
            String[] split = input.split("\n");
            String delimiter = split[0].substring(2);
            String cleaned = split[1];
            separators.add(delimiter);
            return separateAll(cleaned);
        }

        return separateAll(input);
    }

    private Stream<PositiveNumber> separateAll(String input) {
        String regex = "[" + String.join("", separators) + "]";
        String[] split = input.split(regex);
        return Arrays.stream(split)
                .map(PositiveNumber::new);
    }
}
