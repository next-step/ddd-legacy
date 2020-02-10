package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String input) {
        if(isNullOrEmpty(input)) {
            return 0;
        }
        String[] operands =  getOperandsCustomDelimiter(input);
        if(operands == null) {
            operands = getOperandsDefaultDelimiter(input);
        }
        return add(operands);
    }

    private boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    private String[] getOperandsCustomDelimiter(String input) {
        Pattern pattern = Pattern.compile("//(.)\n(.*)");
        Matcher matcher = pattern.matcher(input);
        if(matcher.find()) {
            String delimiter = matcher.group(1);
            return matcher.group(2).split(delimiter);
        }
        return null;
    }

    private String[] getOperandsDefaultDelimiter(String input) {
        String delimiter = ",|:";
        return input.split(delimiter);
    }

    private int add(String[] operands) throws RuntimeException {
        int result = 0;
        for (String operand : operands) {
            result += transform(operand);
        }
        return result;
    }

    private int transform(String number) {
        int resultNumber;
        try {
            resultNumber = Integer.parseInt(number);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            throw new RuntimeException("operand is not number format");
        }
        if(resultNumber < 0) {
            throw new RuntimeException("operand is negative number");
        }
        return resultNumber;
    }
}
