package kitchenpos;

import java.util.Arrays;

import static org.junit.platform.commons.util.StringUtils.isBlank;

public class StringCalculator {
    public int add(String text) {

        if (isBlank(text)) {
            return 0;
        }

        return splitAndSum(text);
    }

    public int splitAndSum(String text) {
        return Arrays.stream(text.split("[,:]"))
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
