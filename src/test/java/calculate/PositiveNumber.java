package calculate;

import java.util.regex.Pattern;

public class PositiveNumber {

    private static final Pattern pattern = Pattern.compile("\\d+");

    private final int number;

    public PositiveNumber(String number) {
        this(parseInt(number));
    }

    public PositiveNumber(int number) {
        if (number < 1) {
            throw new IllegalArgumentException("숫자는 음수일 수 없습니다.");
        }
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    private static int parseInt(String number) {
        if (!pattern.matcher(number).matches()) {
            throw new IllegalArgumentException("문자가 숫자유형이 아닙니다.");
        }
        return Integer.parseInt(number);
    }
}
