package string_calculator.string_parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomDelimiterStringParser extends StringParser {

    static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    private final String delimiter;

    public CustomDelimiterStringParser(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    protected String[] tokens(String string) {
        final Matcher matcher = pattern.matcher(string);
        assert matcher.matches();
        return matcher.group(2).split(this.delimiter);
    }
}
