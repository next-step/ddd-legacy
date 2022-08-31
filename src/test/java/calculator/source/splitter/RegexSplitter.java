package calculator.source.splitter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSplitter implements StringSplitter {
    private final Pattern pattern;

    public RegexSplitter(final String regex) {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public List<String> split(final String input) {
        Matcher m = pattern.matcher(input);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return List.of(m.group(2).split(customDelimiter));
        }
        return List.of();
    }


}
