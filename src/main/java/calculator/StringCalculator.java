package calculator;

public class StringCalculator {

    public int inputText(String text) {
        if (ValidationUtils.checkNull(text)) {
            return 0;
        }
        return add(ValidationUtils.splitValue(text));
    }

    public int add(String[] values) {
        int result = 0;
        for (String value : values) {
            PositiveInteger positiveInteger = new PositiveInteger(Integer.parseInt(value));
            result += positiveInteger.getValue();
        }
        return result;
    }
}
