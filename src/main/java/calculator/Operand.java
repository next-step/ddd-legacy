package calculator;

public class Operand {
    private final int number;

    private Operand(int value) {
        this.number = value;
    }

    public static Operand valueOf(int value) {
        if (0 > value) {
            throw new IllegalArgumentException("음수는 추가될 수 없습니다.");
        }

        return new Operand(value);
    }

    public Operand add(Operand operand) {
        return new Operand(this.number + operand.number);
    }

    public int intValue() {
        return this.number;
    }
}
