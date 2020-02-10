package calculator;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {

    private static final String DELIMITER_REGEX = ",|:";
    private static final String CUSTOMED_DELIMITER_MATCHING_REGEX = "//(.)\n(.*)";
    private static final Pattern CUSTOM_SPLIT_REGEX = Pattern.compile(CUSTOMED_DELIMITER_MATCHING_REGEX);


    private int result = 0;

    public StringCalculator (){ }

    public int add(String text) {

        if (isEmptyOrNull(text)) {
            return result;
        }

        List<Integer> numbers = parseToInteger(extractNumberByDelimiter(text));

        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    private boolean isEmptyOrNull (String text){
        return StringUtils.isEmpty(text);
    }

    private String[] extractNumberByDelimiter(String text){

        Matcher m = CUSTOM_SPLIT_REGEX.matcher(text);

        return m.find() ?
                m.group(2).split(m.group(1)) : text.split(DELIMITER_REGEX);
    }

    private List<Integer> parseToInteger (String[] stringNumbers){

        return Arrays.stream(stringNumbers)
                .map(strings -> getPositiveNumber(strings))
                .collect(Collectors.toList());
    }

    private int getPositiveNumber(String stringNumber){
        int number = 0;

        try{
            number = Integer.parseInt(stringNumber);
        }catch (RuntimeException e){
            throw new RuntimeException();
        }

        return validateNaturalNumber(number);
    }

    private int validateNaturalNumber(int number){
        if(number <= 0){
            throw new RuntimeException();
        }
        return number;
    }
}
