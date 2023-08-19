package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OperandParser {
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String BASIC_PATTERN = "[,:]";

    public Operands extractOperands(String value) {
        if (CUSTOM_PATTERN.matcher(value).find()) {
            return parseCustomPattern(value);
        }

        return parseBasicPattern(value);
    }

    private Operands parseBasicPattern(String value) {
        String[] tokens = value.split(BASIC_PATTERN);
        return toList(tokens);
    }

    private Operands parseCustomPattern(String value) {
        Matcher matcher = CUSTOM_PATTERN.matcher(value);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            String[] tokens = matcher.group(2).split(customDelimiter);
            return toList(tokens);
        }
        return new Operands();
    }

    private Operands toList(String[] tokens) {
        List<Operand> operands = Arrays.stream(tokens)
                .mapToInt(Integer::parseInt)
                .boxed()
                .map(Operand::valueOf)
                .collect(Collectors.toList());
        return new Operands(operands);
    }
}
