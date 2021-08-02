package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextParser {
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    public static List<Number> parseToExtractNumbers(String text) {
        var delimiters = new Delimiters(List.of(',', ';'));
        var matcher = pattern.matcher(text);
        var number = text;
        if (matcher.find()) {
            delimiters.add(matcher.group(1).charAt(0));
            number = matcher.group(2);
        }

        return Arrays.stream(number.split(delimiters.toRegex()))
            .map(Number::new)
            .collect(Collectors.toUnmodifiableList());
    }
}
