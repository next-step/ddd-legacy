package calculator;

import javax.persistence.criteria.CriteriaBuilder;

public class Number {

    private final int value;

    public Number(int value) {
        this.value = value;
    }

    public Number(String value) {
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return value;
    }

    public Number add(Number number) {
        return new Number(value + number.getValue());
    }

    public boolean isNegative() {
        return value < 0;
    }

    public static Number zero() {
        return new Number(0);
    }

}
