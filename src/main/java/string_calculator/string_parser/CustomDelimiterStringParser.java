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
        if (!matcher.matches()) {
            throw new IllegalArgumentException("입력값이 //(.)\n(.*) 패턴과 일치하지 않습니다");
        }
        return matcher.group(2).split(this.delimiter);
    }
}
