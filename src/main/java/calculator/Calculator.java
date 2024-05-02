package calculator;

import java.util.Arrays;

public class Calculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) return 0;

        String[] numbers = text.split("[,|;]");
        return Arrays.stream(numbers).mapToInt(Integer::parseInt).sum();
    }
}
