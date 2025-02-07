package StringCalculator;

public class Number {
    private final int value;

    public Number(String text) {
        this.value = parse(text);
    }

    private int parse(String text) {
        int number = Integer.parseInt(text);
        validate(number);
        return number;
    }

    private void validate(int number) {
        if (number < 0) {
            throw new RuntimeException("음수는 허용되지 않습니다.");
        }
    }

    public int getValue() {
        return value;
    }
} 
