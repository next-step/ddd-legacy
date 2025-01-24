package calculator;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.micrometer.common.util.StringUtils.isBlank;

public class StringCalculator {
    public int add(String input) {
        DelimiterParser delimiterParser = new DelimiterParser();

        if (isEmptyString(input)) {
            return 0;
        }

        NumberGroups parse = delimiterParser.parse(input);

        return parse.sum();
    }

    private boolean isEmptyString(String input) {
        return input == null || input.isBlank();
    }
}
