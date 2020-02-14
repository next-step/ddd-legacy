package calculator;

import calculator.model.CalcNumber;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public class StringCalculator {

    public StringCalculator() {
    }

    public boolean isEmptyInput(String input) {
        return Strings.isEmpty(input);
    }

    public int add(String input) {
        if (isEmptyInput(input)) {
            return CalcNumber.DEFAULT_CALC_VALUE.getValue();
        }

        List<String> parsedStrings = StringSplitter.split(input);

        CalcNumber sum = parsedStrings
                .stream()
                .map(CalcNumber::new)
                .reduce(CalcNumber::sum)
                .orElseGet(() -> CalcNumber.DEFAULT_CALC_VALUE);

        return sum.getValue();

    }


}
