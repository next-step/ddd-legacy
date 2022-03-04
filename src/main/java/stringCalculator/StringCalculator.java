package stringCalculator;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator implements Calculator {

    private static final int ZERO = 0;
    private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";
    private static final Pattern CALC_PATTERN = Pattern.compile(CUSTOM_DELIMITER);

    private static final String DEFAULT_SEPARATOR = ",|:";
    private static final Pattern DEFAULT_PATTERN = Pattern.compile(DEFAULT_SEPARATOR);

    @Override
    public int add(String text) {

        if (StringUtils.isBlank(text))
            return ZERO;

        List<PositiveNumber> listPositiveNumbers = getPositiveNumbersListCustomMatchPattern(text);
        if (!listPositiveNumbers.isEmpty()) {
            return listPositiveNumbers.stream()
                                        .map(pn -> pn.getPositiveNumber())
                                        .reduce(0, Integer::sum);
        }

        listPositiveNumbers = getPositiveNumbersListDefaultMatchPattern(text);
        if (!listPositiveNumbers.isEmpty()) {
            return listPositiveNumbers.stream()
                    .map(pn -> pn.getPositiveNumber())
                    .reduce(0, Integer::sum);
        }

        return new PositiveNumber(text).getPositiveNumber();
    }

    private List<PositiveNumber> getPositiveNumbersListCustomMatchPattern(String text) {

        Matcher matcher = CALC_PATTERN.matcher(text);
        if (!matcher.find())
            return Collections.emptyList();

        return Arrays.stream(
                    matcher.group(2).split(matcher.group(1))
                )
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    private List<PositiveNumber> getPositiveNumbersListDefaultMatchPattern(String text) {

        Matcher matcher = DEFAULT_PATTERN.matcher(text);
        if (!matcher.find())
            return Collections.emptyList();

        return Arrays.stream(
                    text.split(DEFAULT_SEPARATOR)
                )
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

}
