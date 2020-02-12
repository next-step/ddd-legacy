package calculator;

public class Number {
    private String textNumber;

    public Number(String textNumber) {
        this.textNumber = textNumber;
    }

    private int getPositiveNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("음수 입력 에러");
        }
        return number;
    }

    public int convert() {
        return convertTextToInt(textNumber);
    }

    private int convertTextToInt(String textNumber) {
        try {
            return getPositiveNumber(Integer.parseInt(textNumber));
        } catch (Exception e) {
            throw new IllegalArgumentException("숫자가 아닙니다.");
        }
    }
}
