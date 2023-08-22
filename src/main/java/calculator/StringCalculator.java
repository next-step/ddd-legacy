package calculator;

public class StringCalculator {

    private ValidationUtils validationUtils = new ValidationUtils();

    public int inputText(String text) {
        if (validationUtils.checkNull(text)) {
            return 0;
        }
        return add(validationUtils.splitValue(text));
    }

    public int add(String[] values) {
        int result = 0;
        for (String value : values) {
            validationUtils.numberCheck(value);
            result += Integer.parseInt(value);
        }
        return result;
    }
}
