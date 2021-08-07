package step1.domain;

import step1.common.CalculatorConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Number {

    private final int value;

    public Number(String value) {
        Matcher numberMatcher = Pattern.compile(CalculatorConstant.NUMBER_PATTERN).matcher(value);
        if (!numberMatcher.find()) {
            throw new RuntimeException();
        }
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return this.value;
    }

}
