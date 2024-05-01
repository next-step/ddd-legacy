package calculator.number;

public class Positive {

    private final Number value;

    public Positive(String value) {
        this(new Number(value));
    }

    public Positive(Integer value) {
        this(new Number(value));
    }

    public Positive(Number value) {
        if (value.isNegative()) {
            throw new RuntimeException("음수는 허용되지 않습니다.");
        }
        this.value = value;
    }

}
