package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NumberExtractor {

    private String input;
    private String divider = "[,:]";

    private final Matcher MATCHER;
    private final Pattern REGEX_PATTERN = Pattern.compile("//(.)\n(.*)");

    public NumberExtractor(String input) {
        this.input = input;
        this.MATCHER = REGEX_PATTERN.matcher(input);
    }

    public List<Integer> extractNumbers() {

        checkExtractorMode();

        return Arrays.stream(this.input.split(divider))
                .map(this::stringToInt)
                .collect(Collectors.toList());
    }

    private void checkExtractorMode() {
        if (MATCHER.find()) {
            this.divider = "[,:" + MATCHER.group(1) + "]";
            this.input = MATCHER.group(2);
        }
    }

    private int stringToInt(String input) {
        try {
            return checkMinus(Integer.parseInt(input));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int checkMinus(int number) {
        if (number < 0) throw new RuntimeException();

        return number;
    }
}
