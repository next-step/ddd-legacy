package calculator;

public class StringCalculator {

    public int add(String text) {
        PositiveNumber number = PositiveNumber.zeroNumber();
        if (text == null || text.isBlank()) {
            return 0;
        }

        for (String stringNumber : StringTokenUtils.tokenizer(text)) {
            number.add(new PositiveNumber(stringNumber));
        }

        return number.getNumber();
    }
}
