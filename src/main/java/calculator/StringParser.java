package calculator;

import calculator.exception.NotPositiveNumberException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

public class StringParser {

    private final static String DEFAULT_SEPARATOR = ",|:";
    private final static Pattern PATTERN = Pattern.compile("//(.)\n(.*)");

    public List<Integer> split(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.EMPTY_LIST;
        }

        String separator = DEFAULT_SEPARATOR;
        String numberString = text;
        Matcher m = PATTERN.matcher(text);
        if (m.find()) {
            separator = m.group(1);
            numberString = m.group(2);
        }

        return Arrays.asList(numberString.split(separator)).stream()
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    }

}
