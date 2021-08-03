package stringcalculator;

public class StringCalculator {
    private Formula formula;

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        this.formula = new Formula(text);

        return formula.calculate();
    }
}
