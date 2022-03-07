package stringcalculator;

public class Number {
    private final Integer value;
    private static final int ZERO = 0;

    public Number(Integer input){
        if (input < ZERO) throw new RuntimeException();
        this.value = input;
    }

    public Integer getValue() {
        return value;
    }
}
