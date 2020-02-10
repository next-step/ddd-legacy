package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {
    String defaultPattern = "[,:]";

    public List<String> separateInputs(String input) {
        return Arrays.stream(input.trim()
                .split(defaultPattern))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}