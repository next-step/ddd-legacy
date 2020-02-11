package calculator;

import calculator.model.PositiveNumber;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public class StringCalculator {
    private static final int DEFAULT_INT_VALUE = 0;

    public StringCalculator() {
    }

    public boolean isEmptyInput(String input) {
        return Strings.isEmpty(input);
    }

    public int add(String input) {

        if (isEmptyInput(input)) {
            return DEFAULT_INT_VALUE;
        }

        List<PositiveNumber> numbersList = StringSplitter.split(input);

        return numbersList.stream()
                .mapToInt(PositiveNumber::getNum)
                .sum();
    }


}
