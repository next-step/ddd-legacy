package calculator;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {


    public StringCalculator(){
    }

    public int add(String text){

        if(Objects.isNull(text) || text.isBlank()){
            return 0;
        }

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens= m.group(2).split(customDelimiter);
            return sum(tokens);
        }

        String[] numbers = text.split(",|:");
        return sum(numbers);
    }

    private int sum(String[] numbers){
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
