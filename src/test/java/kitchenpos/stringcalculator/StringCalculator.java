package kitchenpos.stringcalculator;

public class StringCalculator {

    public int add(final String text) {
        final TextCalculateType textCalculateType = TextCalculateType.of(text);

        return calculate(textCalculateType, text);
    }

    private int calculate(TextCalculateType textCalculateType, String text) {
        return textCalculateType.getCalculateFormula().operate(text);
    }

}
