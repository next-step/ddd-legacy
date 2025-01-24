package calculator.domain;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {
    private final Validator validator;

    public StringCalculator(Validator validator) {
        this.validator = validator;
    }

    public int add(String text) {
        if (validator.isEmpty(text)) {
            return 0;
        }

        //커스텀 구분자 처리 (형식: "//;\n1;2;3")
        if (text.startsWith("//")) {
            String[] tokens = text.split("\n", 2);
            String customDelimiter = tokens[0].substring(2);
            return sum(tokens[1].split(customDelimiter));
        }

        //기본 구분자 처리 (",", ":")
        if (text.contains(",") || text.contains(":")) {
            return sum(text.split(",|:"));
        }

        //단일 숫자 반환
        return Integer.parseInt(text);
    }

    private int sum(String[] values) {
        //validator 추가
        List<Integer> list = Arrays.stream(values)
                .map(Integer::parseInt)
                .toList();
        validator.assertNoNegativeNumbers(list);

        return list.stream().mapToInt(Integer::intValue).sum();
    }

    private int parseNumber(String value) {
        int number = Integer.parseInt(value);
        return number;
    }
}
