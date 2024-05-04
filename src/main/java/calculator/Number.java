package calculator;

public class Number {

    private String number;

    public Number(String number) {
        validation(number);
        this.number = number;
    }

    public int getNumber() {
        return Integer.parseInt(number);
    }

    private void validation(String input) {
        if (Integer.parseInt(input) < 0) throw new IllegalArgumentException("입력값은 음수 일 수 없습니다.");
    }
}
