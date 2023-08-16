package string_additional_calculator;

public class Constant {
    public static final Constant ZERO = new Constant(0);

    private final int value;

    private Constant(int value) {
        if (value < 0) {
            throw new RuntimeException(String.format("문자열 계산기에 상수는 음수가 될 수 없습니다. number: %s", value));
        }
        this.value = value;
    }

    public static Constant from(String stringNumber) {
        validate(stringNumber);
        return new Constant(Integer.parseInt(stringNumber));
    }

    private static void validate(String stringNumber) {
        try {
            Integer.parseInt(stringNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. number: %s", stringNumber));
        }
    }

    public Constant sum(Constant constant) {
        return new Constant(this.value + constant.value);
    }

    public int getValue() {
        return this.value;
    }
}
