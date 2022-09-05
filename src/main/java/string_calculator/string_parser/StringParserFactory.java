package string_calculator.string_parser;

import java.util.regex.Matcher;

public class StringParserFactory {

    public StringParser createStringParser(final String string) {
        if (string == null || string.isBlank()) {
            return new SimpleStringParser();
        }

        final Matcher matcher = CustomDelimiterStringParser.pattern.matcher(string);
        if (matcher.matches()) {
            return new CustomDelimiterStringParser(matcher.group(1));
        }

        return new SimpleStringParser();
    }
}
