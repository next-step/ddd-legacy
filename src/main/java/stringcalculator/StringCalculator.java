package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        if(isEmptyOrNull(text)) {
            return 0;
        }

        String[] numbers = StringSplitor.split(text);

        String[] checkedNumbers = NegativeNumbersChecker.checkNumbers(numbers);

        return NumberSummer.sum(checkedNumbers);
    }

    private boolean isEmptyOrNull(String text) {
        return text == null || text.isEmpty();
    }

}
