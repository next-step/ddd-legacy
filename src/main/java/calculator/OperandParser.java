package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum OperandParser {
    BASIC_PARSER((String value) -> toList(value.split("[,:]"))),
    CUSTOM_PARSER(OperandParser::parseCustomPattern);

    private static final String CUSTOM_PATTERN = "//(.)\n(.*)";
    private final Function<String, Operands> parser;

    OperandParser(Function<String, Operands> function) {
        this.parser = function;
    }


    public static Operands extractOperands(String value) {
        if (value.matches(CUSTOM_PATTERN)) {
           return CUSTOM_PARSER.parser.apply(value);
        }

        return BASIC_PARSER.parser.apply(value);
    }

    private static Operands toList(String[] tokens) {
        List<Integer> token = Arrays.stream(tokens)
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(Collectors.toList());
        return new Operands(token);
    }

    private static Operands parseCustomPattern(String value) {
        Matcher matcher = Pattern.compile(CUSTOM_PATTERN).matcher(value);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            String[] tokens = matcher.group(2).split(customDelimiter);
            return toList(tokens);
        }
        return new Operands();
    }

}
