package stringcalculator;

public class PositiveInteger {
    private final int number;

    public PositiveInteger(String number) {
        this(Integer.parseInt(number));
    }

    public PositiveInteger(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수로 생성할 수 없습니다.");
        }
        this.number = number;
    }

    public PositiveInteger add(PositiveInteger added) {
        return new PositiveInteger(this.getNumber() + added.getNumber());
    }

    public int getNumber() {
        return number;
    }
}
