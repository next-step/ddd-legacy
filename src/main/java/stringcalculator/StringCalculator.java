package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        if(isEmptyOrNull(text)) {
            return 0;
        }

        Numbers numbers = new Numbers(StringSplitor.split(text));

        return numbers.sum();
    }

    private boolean isEmptyOrNull(String text) {
        return text == null || text.isEmpty();
    }

}
