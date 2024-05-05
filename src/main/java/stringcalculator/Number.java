package stringcalculator;

public class Number {
    private int number;

    public Number(String value) {
        if(!validate(value)) {
            this.number = 0;
        }
        this.number = Integer.parseInt(value);
    }

    public static Number of(String value) {
        return new Number(value);
    }

    private boolean validate(String value) {
        if(value == null || value.isEmpty()) {
            return false;
        }

        if(Integer.parseInt(value) < 0) {
            throw new RuntimeException("음수는 입력 불가");
        }
        return true;
    }

    public int number() {
        return number;
    }
}
