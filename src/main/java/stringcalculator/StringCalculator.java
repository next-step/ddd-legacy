package stringcalculator;

import io.micrometer.core.instrument.util.StringUtils;

public class StringCalculator {

    public int calculate(String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }
        NumberList numberList = NumberListParser.parse(input);
        return numberList.sum();
    }

}
