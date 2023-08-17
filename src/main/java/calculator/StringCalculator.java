package calculator;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringCalculator {

    public int add(final String text) {
        if (!StringUtils.hasText(text)) {
            return 0;
        }

        if (NumberUtil.isNumber(text) && Integer.parseInt(text) > 0) {
            return Integer.parseInt(text);
        }

        String customDelimiter = "";
        String customRemovedText = text;
        if (text.startsWith("//")) {
            int endDelimiterIdx = text.indexOf("\n");
            customDelimiter = text.substring(2, endDelimiterIdx);
            customRemovedText = text.substring(endDelimiterIdx + 1);
        }

        List<String> nums = Stream.of(customRemovedText.split(",|:|" + customDelimiter))
                                  .filter(StringUtils::hasText)
                                  .collect(Collectors.toList());
        int sum = 0;
        for (String num : nums) {
            if (!NumberUtil.isNumber(num) || Integer.parseInt(num) < 0) {
                throw new RuntimeException("숫자는 숫자 이외의 값 또는 음수를 전달할 수 없습니다여야 합니다.");
            }
            sum += Integer.parseInt(num);
        }
        return sum;
    }
}
