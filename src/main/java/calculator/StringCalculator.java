package calculator;

public class StringCalculator {

    public int run(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }

        Numbers numbers = NumberParser.parse(str);
        return numbers.sum();
    }
}
