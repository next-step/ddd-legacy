package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultNumberExtractor implements NumberExtractor {

    private String pattern = "[,:]";

    public List<Integer> extractNumbers(String input) {

        return Arrays.stream(input.split(pattern))
                .map(number -> {
                    try {
                        int intNumber = Integer.parseInt(number);
                        if (intNumber < 0) throw new RuntimeException();
                        return intNumber;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
