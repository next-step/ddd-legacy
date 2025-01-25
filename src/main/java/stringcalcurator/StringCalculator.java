package stringcalcurator;

import io.micrometer.common.util.StringUtils;

import java.util.List;

public class StringCalculator {

    public int add(String text) {

        if(StringUtils.isBlank(text)) {
            return 0;
        }

        String[] numbers = Separator.splitNumber(text);
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
