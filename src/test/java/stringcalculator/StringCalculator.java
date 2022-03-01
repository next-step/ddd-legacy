package stringcalculator;

public class StringCalculator {


    public int add(String source) {
        if (isNullOrEmpty(source)) {
            return 0;
        }
        return Integer.parseInt(source);
    }

    private boolean isNullOrEmpty(String source) {
        return source == null || source.isEmpty();
    }
}
