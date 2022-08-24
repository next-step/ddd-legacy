package stringcalculator;

import stringcalculator.factory.SplitterFactory;
import stringcalculator.factory.splitter.CustomSplitter;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public StringCalculator() {
    }

    public int sum(final String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new RuntimeException("Null 이거나 공란일 수 없습니다.");
        }

        boolean type = checkSplitterType(value);
        return SplitterFactory.findSplitter(type)
                .split(value)
                .stream().parallel()
                .map(PositiveNumber::new)
                .mapToInt(PositiveNumber::toInt)
                .sum();
    }

    private boolean checkSplitterType(String value) {
        Matcher m = Pattern.compile(CustomSplitter.CUSTOM_DELIMITER_REGEX).matcher(value);
        return m.find();
    }
}
