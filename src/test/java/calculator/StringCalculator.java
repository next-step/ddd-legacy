package calculator;

public class StringCalculator {

    public int add(String text) {
        CalculatorNumber number = CalculatorNumber.zeroNumber();
        if (text == null || text.isBlank()) {
            return 0;
        }

        for (String stringNumber : StringTokenUtils.tokenizer(text)) {
            number.add(new CalculatorNumber(stringNumber));
        }

        return number.getNumber();
    }
}
