package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomSplitStingConvertor implements StingConvertor {

    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_INDEX = 1;
    private static final int NUMBER_INDEX = 2;

    @Override
    public boolean isSupport(String text) {
        return PATTERN.matcher(text).find();
    }

    @Override
    public PositiveNumbers calculate(String text) {
        Matcher matcher = PATTERN.matcher(text);
        matcher.find();
        String customDelimiter = matcher.group(DELIMITER_INDEX);
        List<PositiveNumber> numbers = Arrays.stream(matcher.group(NUMBER_INDEX).split(customDelimiter))
                .map(PositiveNumber::new)
                .collect(Collectors.toList());

        return new PositiveNumbers(numbers);
    }
}
