package stringcalculator;

import java.util.Objects;
import java.util.function.BinaryOperator;

public class Operand {
    private static final int BOUND = 0;
    private final int number;

    public Operand(String number) {
        this(Integer.parseInt(number));
    }

    public Operand() {
        this(0);
    }

    public Operand(int number) {
        validNegative(number);
        this.number = number;
    }

    public Operand operate(Operand operand, BinaryOperator<Integer> operator) {
        return new Operand(operator.apply(number, operand.number));
    }

    public int result() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operand operand = (Operand) o;
        return number == operand.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    private void validNegative(int number) {
        if(number < BOUND) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
    }
}



