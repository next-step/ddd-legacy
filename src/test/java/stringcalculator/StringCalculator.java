package stringcalculator;

public class StringCalculator {
    private final int EMPTY_STRING_RESULT_VALUE = 0;
    private Formula formula;

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return EMPTY_STRING_RESULT_VALUE;
        }
        this.formula = new Formula(text);

        return formula.calculate();
    }
}
