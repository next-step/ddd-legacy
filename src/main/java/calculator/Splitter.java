package calculator;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Splitter {
    private static final List<String> DELIMITER_OF_STANDARD = List.of(",", ":");
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    private final String text;

    public Splitter(String text) {
        this.text = text;
    }

    public Stream<String> splittingText() {
        Matcher matcher = getCustomDelimiterMatcher();
        if (matcher.find()) {
            return Stream.of(matcher.group(2)
                .split(matcher.group(1)));
        }
        return Stream.of(text.split(getStandardDelimiter()));
    }

    private String getStandardDelimiter() {
        return String.format("[%s]"
            , String.join("", DELIMITER_OF_STANDARD));
    }

    private Matcher getCustomDelimiterMatcher() {
        return pattern.matcher(text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Splitter))
            return false;
        Splitter splitter = (Splitter)o;
        return Objects.equals(text, splitter.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
