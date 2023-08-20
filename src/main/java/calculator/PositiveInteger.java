package calculator;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class PositiveInteger {
    private final List<Integer> contents;

    public PositiveInteger(String text, String delimiterRegex) {
        List<String> nums = Stream.of(text.split(delimiterRegex))
                                  .filter(StringUtils::hasText)
                                  .collect(toList());
        validateNumFormat(nums);
        contents = nums.stream()
                       .map(Integer::parseInt)
                       .collect(toList());
    }

    public int sum() {
        return contents.stream()
                       .mapToInt(n -> n)
                       .sum();
    }

    private void validateNumFormat(final List<String> nums) {
        for (String num : nums) {
            if (!isNumber(num) || Integer.parseInt(num) < 0) {
                throw new IllegalArgumentException("숫자는 숫자 이외의 값 또는 음수를 전달할 수 없습니다.");
            }
        }
    }

    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
