package calculator;

import exception.NegativeNumberException;

import java.util.List;

public class StringCalculator {

    /* add : 문자열 덧셈 계산 */
    public Integer add(String input) {

        if (input == null || input.isEmpty()) {
            return 0;
        }

        // 1. 구분자 - 구분자를 기준으로 나누기
        List<String> splits = Delimiter.split(input);

        // 2. 숫자 - 나눈 문자열을 숫자로 변환하기
        Numbers numbers = new Numbers(splits);
        if (numbers.isNullOrEmpty()) {
            throw new IllegalArgumentException("invalid numbers : " + numbers.getNumbers());
        }
        if (numbers.hasNegativeNumber()) {
            throw new NegativeNumberException("negative numbers found : " + numbers.getNumbers());
        }

        // 3. 연산 - 숫자들의 합 구하기
        return numbers.sum();
    }

}
