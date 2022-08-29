package calculator.domain;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSeparator implements Separator {

    private static final String CUSTOM_SEPARATOR_PARSE_PATTERN = "//(.)\n(.*)";
    private static final int CUSTOM_SEPARATOR_INDEX = 1;
    private static final int NUMBERS_INDEX = 2;

    @Override
    public List<String> split(String text) {
        Matcher matcher = Pattern.compile(CUSTOM_SEPARATOR_PARSE_PATTERN).matcher(text);

        if (matcher.find()) {
            String customSeparator = matcher.group(CUSTOM_SEPARATOR_INDEX);
            return List.of(matcher.group(NUMBERS_INDEX).split(customSeparator));
        }

        throw new RuntimeException("구분자 패턴과 알맞는 문자열을 찾지 못했습니다.");
    }

    @Override
    public boolean isMatchWithText(String text) {
        return text.matches(CUSTOM_SEPARATOR_PARSE_PATTERN);
    }
}
