package calculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {

        final int ZERO = 0;
        int result = 0;

        if (text == null || text.isEmpty()) {
            return ZERO;
        }

        if(isNumber(text)){
            result = Integer.parseInt(text);
        }

        if (text.contains(",")) {
            String[] split = text.split(",");
            result= Arrays.stream(split).mapToInt(Integer::parseInt).sum();
        }
        return result;
    }

    private boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }

}
