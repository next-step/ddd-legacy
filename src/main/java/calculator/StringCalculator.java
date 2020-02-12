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
            return new CalcNumber().value;
        }

        List<String> parsedStrings = StringSplitter.split(input);

        CalcNumber sum = parsedStrings
                .stream()
                .map(CalcNumber::new)
                .reduce(CalcNumber::sum)
                .orElse(new CalcNumber());


        return sum.getValue();

    }


}
