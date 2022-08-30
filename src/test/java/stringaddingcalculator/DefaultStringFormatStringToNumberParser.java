package stringaddingcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultStringFormatStringToNumberParser implements StringToNumberParser {
    private static final String DEFAULT_STRING_FORMAT_REGULAR_EXPRESSION = "([0-9][,:]?)+";
    private static final String DEFAULT_SEPARATOR_REGULAR_EXPRESSION = "[,:]";

    @Override
    public boolean isSupport(final String source) {
        return source.matches(DEFAULT_STRING_FORMAT_REGULAR_EXPRESSION);
    }

    @Override
    public List<Integer> parse(final String source) {
        return Arrays.stream(source.split(DEFAULT_SEPARATOR_REGULAR_EXPRESSION))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
