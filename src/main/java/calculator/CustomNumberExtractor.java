package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomNumberExtractor implements NumberExtractor {

    private String pattern = "[,:]";
    private final String REGEX = "//(.)\n(.*)";

    public List<Integer> extractNumbers(String input) {

        if (getCustomDivider(input) != null) {
            pattern = "[,:" + getCustomDivider(input) + "]";
            Matcher regexMatcher = getRegexMatcher(input);
            input = regexMatcher.group(2);
        }

        return Arrays.stream(input.split(pattern))
                .map(number -> {
                    try {
                        int intNumber = Integer.parseInt(number);
                        if (intNumber < 0) throw new RuntimeException();
                        return intNumber;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    protected String getCustomDivider(String input) {
        Matcher regexMatcher = getRegexMatcher(input);

        if (regexMatcher == null) return null;

        return regexMatcher.group(1);
    }

    protected Matcher getRegexMatcher(String input) {
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher;
        }

        return null;
    }
}
