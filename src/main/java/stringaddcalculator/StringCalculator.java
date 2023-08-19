package stringaddcalculator;

import java.util.Objects;

public class StringCalculator {
    public int add(String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            return 0;
        }

        if (text.length() == 1) {
            return Integer.parseInt(text);
        }

        String[] tokens = text.split(",|:");

        int result = 0;

        for (String value : tokens) {
            int num = Integer.parseInt(value);
            if (num < 0) {
                throw new RuntimeException("음수 입력 안 됨");
            }
            result = result + num;
        }
        return result;
    }
}
