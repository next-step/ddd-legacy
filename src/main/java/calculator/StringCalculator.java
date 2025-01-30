package calculator;

import java.util.Arrays;
import java.util.Objects;

public class StringCalculator {

    private Integer result = 0;

    /* add : 문자열 덧셈 계산 */
    public Integer add(String input) {

        if (input == null || input.isEmpty()) {
            return 0;
        }

        // 1. 구분자 - 구분자를 기준으로 나누기
        String[] splitNums = Delimiter.split(input);

        // 2. 숫자 - 나눈 문자열을 숫자로 변환하기
        // 3. 연산 - 숫자들의 합 구하기
        result = Arrays.stream(splitNums)
                .map(Number::convertNumber)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return result;
    }

}
