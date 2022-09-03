package string_calculator.string_parser;

import java.util.List;
import string_calculator.NonNegativeLong;

public interface StringParser {

    List<NonNegativeLong> parse(final String string);
}
