package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser<T> {
    private static final String PATTERN_FORMAT = "//(.)\n(.*)";
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final int TEXT_INDEX = 2;
    private static final int DELIMITER_INDEX = 1;

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(PATTERN_FORMAT);

    private final TextConverter<T> textConverter;

    public TextParser(TextConverter<T> textConverter) {
        this.textConverter = textConverter;
    }

    public List<T> parse(String text) {
        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (m.find()) {
            return textConverter.convertToList(m.group(TEXT_INDEX), Pattern.quote(m.group(DELIMITER_INDEX)));
        }
        return textConverter.convertToList(text, DEFAULT_DELIMITER);
    }
}
