package stringcalculator;

import java.util.Arrays;
import java.util.List;

public class StringCalculator {
    private static final String DELIMITERS = "[,:]";

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        String[] split = text.split(DELIMITERS);
        List<Integer> result = Arrays.stream(split)
                                     .map(Integer::parseInt)
                                     .toList();

        if (result.stream().anyMatch(it -> it < 0)) {
            throw new RuntimeException("음수는 처리하지 않습니다");
        }
        return result.stream().reduce(0, Integer::sum);
    }
}
