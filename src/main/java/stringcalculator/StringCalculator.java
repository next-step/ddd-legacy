package stringcalculator;

public class StringCalculator {

    public int add(String text) {
        if (isEmpty(text)) {
            return 0;
        }

        return Operations.sum(Numbers.toNumbers(Delimiter.textToTokens(text)));
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }
}
