package stringaddingcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomSeparatorFormatStringToNumberParser implements StringToNumberParser {
    private static final Pattern CUSTOM_SEPARATOR_FORMAT_STRING_PATTERN = Pattern.compile("//(.)\\\\n(.+)");
    private static final String COLON_SEPARATOR = ":";

    @Override
    public boolean isSupport(final String source) {
        return source.matches(CUSTOM_SEPARATOR_FORMAT_STRING_PATTERN.pattern());
    }

    @Override
    public List<Integer> parse(final String source) {
        final Matcher matcher = CUSTOM_SEPARATOR_FORMAT_STRING_PATTERN.matcher(source);
        if (matcher.find()) {
            final String replacedSeparatorSource = matcher.group(2).replace(matcher.group(1), COLON_SEPARATOR);
            return Arrays.stream(replacedSeparatorSource.split(COLON_SEPARATOR))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
