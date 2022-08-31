package calculator.domain;

import java.util.List;
import java.util.regex.Pattern;

public class DefaultSeparator implements Separator {

    public static final Pattern DEFAULT_SEPARATOR_PARSE_PATTERN = Pattern.compile("[,:]");

    @Override
    public List<String> split(String text) {
        return List.of(DEFAULT_SEPARATOR_PARSE_PATTERN.split(text));
    }

    @Override
    public boolean isMatchWithText(String text) {
        return false;
    }
}
