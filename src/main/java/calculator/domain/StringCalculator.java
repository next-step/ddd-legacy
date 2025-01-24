package calculator.domain;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class StringCalculator {

    public int add(String text) {
        if (StringUtils.isEmpty(text)) {
            return 0;
        }

        //음수 처리
        if (text.contains("-")) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
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
        return Arrays.stream(values)
                .mapToInt(this::parseNumber)
                .sum();
    }

    private int parseNumber(String value) {
        int number = Integer.parseInt(value);
        if (number < 0) {
            throw new RuntimeException("음수는 입력할 수 없습니다." + number);
        }
        return number;
    }
}
