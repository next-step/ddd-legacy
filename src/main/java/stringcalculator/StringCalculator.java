package stringcalculator;

public class StringCalculator {

    public int add(String text) {
        if (isEmpty(text)) {
            return 0;
        }

        return Operations.sum(Numbers.textToNumbers(text));
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }
}
