package string_calculator;

import java.util.List;
import string_calculator.string_parser.StringParser;

public class StringCalculator {

    private final StringParser stringParser;

    public StringCalculator(StringParser stringParser) {
        this.stringParser = stringParser;
    }

    public long calculate(final String string) {
        List<NonNegativeLong> list = this.stringParser.parse(string);
        return new ListCalculator(list).sum();
    }
}
