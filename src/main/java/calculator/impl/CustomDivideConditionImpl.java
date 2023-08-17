package calculator.impl;

import calculator.DivideCondition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomDivideConditionImpl implements DivideCondition {
    @Override
    public String[] divide(String value) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(value);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return new String[]{};
    }

}
