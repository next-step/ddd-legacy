package calculator;

import kitchenpos.common.utils.StringUtils;

import static kitchenpos.common.utils.NumberUtils.fromStringArrayConvertToPositiveNumberArray;
import static kitchenpos.common.utils.NumberUtils.sum;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return sum(extractPositiveNumbers(text));
    }

    private int[] extractPositiveNumbers(String text) {
        return fromStringArrayConvertToPositiveNumberArray(splitText(text));
    }

    private String[] splitText(String text) {
        return StringUtils.filterTextByDelimiter(text);
    }

}
