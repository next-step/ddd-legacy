package stringcalcurator;

import io.micrometer.common.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {

        if(StringUtils.isBlank(text)) {
            return 0;
        }

        String splitFilter = Separator.compileSeparator(text);
        String[] numbers = Separator.removeIfCustomSeparator(text).split(splitFilter);
        List<StringNumber> numberList = StringNumberList.create(numbers).getIntNumbers();

        return calculateNumbers(numberList);
    }

    private int calculateNumbers(List<StringNumber> numbers) {
        return numbers.stream()
                .map(StringNumber::getNumber)
                .reduce(Integer::sum)
                .orElseGet(() -> 0);
    }

}
