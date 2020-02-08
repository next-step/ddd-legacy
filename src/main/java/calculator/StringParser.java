package calculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

public class StringParser {

    private final static String DEFAULT_SEPARATOR = ",|:";
    private final static Pattern SPLIT_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_INDEX = 1;
    private static final int STRING_NUMBER_INDEX = 2;

    public List<PositiveNumber> split(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.EMPTY_LIST;
        }

        String separator = DEFAULT_SEPARATOR;
        String numberString = text;
        Matcher m = SPLIT_PATTERN.matcher(text);
        if (m.find()) {
            separator = m.group(DELIMITER_INDEX);
            numberString = m.group(STRING_NUMBER_INDEX);
        }

        return Arrays.asList(numberString.split(separator)).stream()
            .map(Integer::parseInt)
            .map(PositiveNumber::new)
            .collect(Collectors.toList());
    }

}
