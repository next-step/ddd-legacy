package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String DELIMITER_REGEX = ",|:";
    private static final String CUSTOMED_DELIMITER_REGEX = "//(.)\n(.*)";

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
        if(text == null || text.isEmpty()){
            return true;
        }
        return false;
    }

    private String[] extractNumberByDelimiter(String text){

        Matcher m = Pattern.compile(CUSTOMED_DELIMITER_REGEX).matcher(text);
        if(m.find()){
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }else{
            return text.split(DELIMITER_REGEX);
        }
    }

    private List<Integer> parseToInteger (String[] stringNumbers){

        List<Integer> numbers = new ArrayList<>();

        for(String stringNumber : stringNumbers){
            int number = getPositiveNumber(stringNumber);
            numbers.add(number);
        }

        return numbers;
    }

    private int getPositiveNumber(String stringNumber){
        int number = 0;

        try{
            number = Integer.parseInt(stringNumber);
        }catch (RuntimeException e){
            e.printStackTrace();
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
