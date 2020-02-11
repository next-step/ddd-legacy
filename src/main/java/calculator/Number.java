package calculator;

public class Number {
    private final int number;

    public Number(String textNumber) {
        this.number = convertTextToInt(textNumber);
    }

    public int getNumber() {
        return number;
    }

    private int convertTextToInt(String text) {
        try {
            return getPostiveNumber(Integer.parseInt(text));
        } catch (Exception e) {
            throw new IllegalArgumentException("숫자가 아닙니다.");
        }
    }

    private int getPostiveNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("음수 입력 에러");
        }
        return number;
    }
}
