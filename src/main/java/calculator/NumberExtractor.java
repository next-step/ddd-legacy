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

    public NumberExtractor(String input) {
        this.input = input;
        Pattern pattern = Pattern.compile("//(.)\n(.*)");
        this.MATCHER = pattern.matcher(input);
    }

    public List<Integer> extractNumbers() {

        checkExtractorMode();

        return Arrays.stream(this.input.split(divider))
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

    protected void checkExtractorMode() {
        if (MATCHER.find()) {
            this.divider = "[,:" + MATCHER.group(1) + "]";
            this.input = MATCHER.group(2);
        }
    }
}
