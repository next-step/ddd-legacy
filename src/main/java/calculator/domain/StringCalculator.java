package calculator.domain;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private String delimiter = ",|:";

    public StringCalculator() {
    }

    public int add(String inputString) {
        if(StringUtils.isEmpty(inputString)) {
            return 0;
        }

        String[] tokens = getTokens(inputString);

        List<CalculatorNumber> calculatorNumbers = Arrays.stream(tokens)
                .map(CalculatorNumber::new)
                .collect(Collectors.toList())
                ;

        CalculatorNumberGroups calculatorNumberGroups = new CalculatorNumberGroups(calculatorNumbers);

        return calculatorNumberGroups.addAllCalculatorNumber();
    }

    private String[] getTokens(String inputString) {
        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(inputString);
        if (m.find()) {
            addDelimeter(m.group(1));
            return m.group(2).split(delimiter);
        }
        return inputString.split(delimiter);
    }

    private void addDelimeter(String substring) {
        this.delimiter += "|" + substring;
    }
}
