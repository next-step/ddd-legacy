package stringcalculator;

import java.util.Arrays;
import java.util.Optional;

public class StringCalculator {
    public int  add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        String[] split = text.split(",");
        Optional<Integer> result = Arrays.stream(split).map(Integer::parseInt).reduce((a, b) -> a + b);
        if (result.isPresent()) {
            return result.get();
        }
        return -1;
    }
}
