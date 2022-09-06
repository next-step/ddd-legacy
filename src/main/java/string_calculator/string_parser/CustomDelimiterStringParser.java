package string_calculator.string_parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import string_calculator.NonNegativeLong;

public class CustomDelimiterStringParser implements StringParser {

    static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    private final String delimiter;

    public CustomDelimiterStringParser(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public List<NonNegativeLong> parse(String string) {
        if (string == null || string.isBlank()) {
            return new ArrayList<>();
        }

        final Matcher matcher = pattern.matcher(string);
        assert matcher.matches();
        final String[] tokens = matcher.group(2).split(this.delimiter);

        return Arrays.stream(tokens)
                .map(NonNegativeLong::new)
                .collect(Collectors.toUnmodifiableList());
    }
}
