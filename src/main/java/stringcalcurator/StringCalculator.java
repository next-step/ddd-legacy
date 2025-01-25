package stringcalcurator;

import io.micrometer.common.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private final String DEFAULT_SPLIT_REGEX = "[,:]";
    private final String CUSTOM_SPLIT_REGEX = "//(.)\n(.*)";

    public int add(String text) {

        if(StringUtils.isBlank(text)) {
            return 0;
        }

        Matcher matcher = Pattern.compile(CUSTOM_SPLIT_REGEX).matcher(text);
        String splitFilter = DEFAULT_SPLIT_REGEX;
        if(matcher.find()){
            splitFilter = matcher.group(1);
            text = matcher.group(2);
        }

        String[] numbers = text.split(splitFilter);

        List<Integer> numberList = StringNumberList.create(numbers).getIntNumbers();

        return calculateNumbers(numberList);
    }

    private int calculateNumbers(List<Integer> numbers) {
        int result = 0;
        for (int number : numbers) {
            result += number;
        }
        return result;
    }

}
