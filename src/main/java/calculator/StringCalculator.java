package calculator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private final Pattern customDelimiterFindingPattern = Pattern.compile("//(.)\n(.*)");

    public int add(String input) {
        if(isNullOrEmpty(input)) {
            return 0;
        }
        String[] operandStrings =  getOperandsCustomDelimiter(input);
        if(operandStrings == null) {
            operandStrings = getOperandsDefaultDelimiter(input);
        }
        Operands Operands = new Operands(operandStrings);
        return Operands.sum();
    }

    private boolean isNullOrEmpty(String text) {
        return Objects.isNull(text) || text.isEmpty();
    }

    private String[] getOperandsCustomDelimiter(String input) {
        Matcher matcher = customDelimiterFindingPattern.matcher(input);
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

}
