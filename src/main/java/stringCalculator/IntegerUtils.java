package stringCalculator;

public final class IntegerUtils {

    private IntegerUtils() {
    }

    public static int parsePositiveInt(String text) {
        int textToInt = Integer.parseInt(text);
        if (textToInt < 0) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
        return textToInt;
    }

}
