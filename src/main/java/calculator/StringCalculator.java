package calculator;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {
    private int result = 0;

    public StringCalculator (){ }

    public int add(String text) {

        if (isEmptyOrNull(text)) {
            return result;
        }

        List<PositiveInteger> numbers = parseToInteger(StringSplitter.extractNumberStringByDelimiter(text));

        return numbers.stream()
                .mapToInt(PositiveInteger::valueOf)
                .sum();
    }

    private boolean isEmptyOrNull (String text){
        return StringUtils.isEmpty(text);
    }

    private List<PositiveInteger> parseToInteger (String[] stringNumbers){

        return Arrays.stream(stringNumbers)
                .map(strings -> getPositiveNumber(strings))
                .collect(Collectors.toList());
    }

    private PositiveInteger getPositiveNumber(String stringNumber){
        int number = 0;

        try{
            number = Integer.parseInt(stringNumber);
        }catch (RuntimeException e){
            throw new RuntimeException();
        }
        return new PositiveInteger(number);
    }
}
