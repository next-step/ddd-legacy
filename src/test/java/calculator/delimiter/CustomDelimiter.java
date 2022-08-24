package calculator.delimiter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomDelimiter implements Delimiter {
    private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";

    @Override
    public List<String> split(List<String> expressions) {
        return expressions.stream()
                          .map(expression -> split(expression))
                          .flatMap(List::stream)
                          .collect(Collectors.toList());
    }


    private List<String> split(String expression) {
        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER).matcher(expression);
        if (matcher.find()) {
            String delimiter = matcher.group(1);
            String[] tokens = matcher.group(2).split(delimiter);
            return Arrays.asList(tokens);
        }
        return Arrays.asList(expression);
    }

}
