package kitchenpos.stringcalculator;

public class StringCalculator {

    public int add(final String text) {
        final TextCalculateTypes textCalculateTypes = TextCalculateTypes.of(text);

        return calculate(textCalculateTypes, text);
    }
    private int calculate(TextCalculateTypes textCalculateTypes, String text) {
        return textCalculateTypes.getCalculateFormula().operate(text);
    }

}
