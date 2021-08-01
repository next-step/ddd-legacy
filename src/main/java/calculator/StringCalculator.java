package calculator;

public class StringCalculator {

    public static final int DEFAULT_VALUE = 0;

    public int add(final String text) {
        if (this.isEmpty(text)) {
            return DEFAULT_VALUE;
        }

        Delimiter delimiter = new StringDelimiter();
        CalculateStrategy sum = new SumCalculateStrategy();

        String[] textArray = delimiter.parse(text);

        return sum.calculate(textArray);
    }

    private boolean isEmpty(final String text) {
        return text == null || text.isEmpty();
    }
}
