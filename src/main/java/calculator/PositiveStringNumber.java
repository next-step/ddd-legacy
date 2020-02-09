package calculator;

public class PositiveStringNumber {
    int number;

    public PositiveStringNumber(String input) {
        int intNumber = parseInt(input);
        if (intNumber < 0) throw new RuntimeException("음수는 들어올 수 없습니다");
        this.number = intNumber;
    }

    public int getNumber() {
        return number;
    }

    public static int parseInt(String s) throws NumberFormatException {
        return Integer.parseInt(s, 10);
    }
}
