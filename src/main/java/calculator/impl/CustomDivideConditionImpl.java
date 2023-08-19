package calculator.impl;

import calculator.DivideCondition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomDivideConditionImpl implements DivideCondition {
    private static final Pattern COMPILED_PATTERN = Pattern.compile("//(.)\n(.*)");

    @Override
    public String[] divide(String value) {
        Matcher m = COMPILED_PATTERN.matcher(value);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return new String[]{};
    }

    public boolean supports(String value) {
        return COMPILED_PATTERN.matcher(value).find();
    }
}
