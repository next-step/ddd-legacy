package stringcalculator;

public class OnlyPositiveNumber {
    private int number;

    public OnlyPositiveNumber(String value) {
        if(!validate(value)) {
            this.number = 0;
        } else {
            this.number = Integer.parseInt(value);
        }
    }

    public static OnlyPositiveNumber of(String value) {
        return new OnlyPositiveNumber(value);
    }

    private boolean validate(String value) {
        if(value == null || value.isEmpty()) {
            return false;
        }
        if(!value.matches("\\d+")) {
            throw new IllegalArgumentException("올바른 숫자값을 입력하세요.");
        }
        if(Integer.parseInt(value) < 0) {
            throw new IllegalArgumentException("음수는 입력 불가");
        }
        return true;
    }

    public int number() {
        return number;
    }
}
