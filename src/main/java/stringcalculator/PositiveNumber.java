package stringcalculator;

public class PositiveNumber {
    private final int value;

    public PositiveNumber(String number) {
        int num = Integer.parseInt(number);
        validate(num);
        this.value = num;
    }

    private void validate(int num) {
        if (num < 0) {
            throw new RuntimeException();
        }
    }

    public int getValue(){
        return value;
    }
}
