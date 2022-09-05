package string_calculator;

import java.util.List;
import string_calculator.string_parser.StringParser;
import string_calculator.string_parser.StringParserFactory;

public class StringCalculator {

    public long calculate(final String string) {
        final StringParserFactory stringParserFactory = new StringParserFactory();
        final StringParser stringParser = stringParserFactory.createStringParser(string);

        final List<NonNegativeLong> list = stringParser.parse(string);
        return new ListCalculator(list).sum();
    }
}
