package calculator;

public class StringAddCalculator implements Calculator<Integer, String> {
    private final PartsGenerator partsGenerator;

    public StringAddCalculator(PartsGenerator partsGenerator) {
        this.partsGenerator = partsGenerator;
    }

    @Override
    public Integer calculate(String input) {
        if (input == null || "".equals(input)) {
            return 0;
        }
        Parts parts = partsGenerator.generate(input);
        return parts.intParts()
                .stream()
                .reduce(0, Integer::sum);
    }
}
