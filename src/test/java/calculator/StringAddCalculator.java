package calculator;

public class StringAddCalculator implements Calculator<Integer, String> {

    public StringAddCalculator() {
    }

    @Override
    public Integer calculate(String input) {
        if (input == null || "".equals(input)) {
            return 0;
        }
        StringParts stringParts = new StringParts(input);
        return stringParts.toNumbers()
                .stream()
                .reduce(0, Integer::sum);
    }
}
