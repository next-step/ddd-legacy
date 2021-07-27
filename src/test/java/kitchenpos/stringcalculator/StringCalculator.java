package kitchenpos.stringcalculator;

public class StringCalculator {

    public int add(final String text) {
        return TextCalculateTypes.of(text).calculate(text);
    }

}
