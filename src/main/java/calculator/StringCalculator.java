package calculator;

public class StringCalculator {

    public int run(String str) {
        if (str == null || str.isBlank()) {
            return Number.ZERO.getValue();
        }

        Numbers numbers = NumberParser.parse(str);
        return numbers.sum();
    }
}
