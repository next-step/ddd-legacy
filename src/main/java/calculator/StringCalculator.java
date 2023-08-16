package calculator;

public class StringCalculator {

    public int run(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }

        PositiveNumbers positiveNumbers = NumberParser.parse(str);
        return positiveNumbers.sum();
    }
}
