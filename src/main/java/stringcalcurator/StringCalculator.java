package stringcalcurator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        if(text == null || text.isEmpty()) {
            return 0;
        }

        String splitFilter = "[,:]";
        String CUSTOM_SPLIT_REGEX = "//(.)\n(.*)";
        Matcher matcher = Pattern.compile(CUSTOM_SPLIT_REGEX).matcher(text);

        if(matcher.find()){
            splitFilter = matcher.group(1);
            text = matcher.group(2);
        }

        String[] numbers = text.split(splitFilter);

        return calculateNumbers(validate(numbers));
    }

    private int calculateNumbers(List<Integer> numbers) {
        int result = 0;
        for (int number : numbers) {
            result += number;
        }
        return result;
    }

    private List<Integer> validate(String[] numbers){
        List<Integer> result = new ArrayList<>();
        for(String number : numbers){
            int numberInt = Integer.parseInt(number);
            if(numberInt < 0){
                throw new RuntimeException();
            }
            result.add(numberInt);
        }
        return result;
    }

}
