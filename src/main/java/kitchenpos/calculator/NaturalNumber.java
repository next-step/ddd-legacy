package kitchenpos.calculator;

public class NaturalNumber {
    private final int value;

    private NaturalNumber(int number) {
        throwOnLessThanOrEqualToZero(number);
        this.value = number;
    }

    public NaturalNumber(String numberStr) {
        int number = toNumber(numberStr);
        throwOnLessThanOrEqualToZero(number);
        this.value = number;
    }

    public static NaturalNumber add(NaturalNumber x, NaturalNumber y) {
        return new NaturalNumber(x.value + y.value);
    }

    public int getValue() {
        return value;
    }

    private int toNumber(String numberStr) {
        int number;
        try {
            number = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자를 입력해야 합니다.");
        }

        return number;
    }

    private void throwOnLessThanOrEqualToZero(int number) {
        if (number <= 0) {
            throw new RuntimeException("숫자가 양수여야 합니다.");
        }
    }
}
