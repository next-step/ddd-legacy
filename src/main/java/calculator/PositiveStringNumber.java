package calculator;

public class PositiveStringNumber {
    private int number;

    public PositiveStringNumber(String input) {
        int intNumber = parseInt(input);
        if (intNumber < 0) throw new RuntimeException("음수는 들어올 수 없습니다");
        this.number = intNumber;
    }

    public int getNumber() {
        return number;
    }

    private int parseInt(String s) {
        return Integer.parseInt(s, 10);
    }
}
