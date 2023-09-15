package stringaddcalculator;

public class Operand {
    public static final Operand ZERO = new Operand(0);

    private final int number;

    public Operand(int number) {
        if (number < 0) {
            throw new NegativeOperandException(number);
        }
        this.number = number;
    }

    public Operand plus(Operand other) {
        return new Operand(number + other.number);
    }

    public int getNumber() {
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
        return number;
    }
}