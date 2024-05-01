package calculator.number;

import utils.*;

import java.util.*;

public class Number {

    private final int value;

    private static final int ZERO = 0;

    public Number(Integer value) {
        this.value = Optional.ofNullable(value).orElse(ZERO);
    }

    public Number(String value) {
        this.value = parse(value);
    }

    private Integer parse(String text) {
        if (StringUtils.isBlankWhenTrim(text)) {
            return Integer.parseInt(text);
        }

        return ZERO;
    }

}
