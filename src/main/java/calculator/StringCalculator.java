package calculator;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

public class StringCalculator {
    private static final String DEFAULT_SEPARATOR = ",|:";

    public int add(String text) {
        if (StringUtils.isEmpty(text)) {
            return 0;
        }
        List<Integer> numbers = split(text);
        return sum(numbers);
    }

    private List<Integer> split(String text) {
        String separator = DEFAULT_SEPARATOR;
        String numberString = text;
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            separator = m.group(1);
            numberString = m.group(2);
        }

        return Arrays.asList(numberString.split(separator)).stream()
            .map(this::parse)
            .collect(Collectors.toList());
    }

    private Integer parse(String token) {
        try {
            Integer integer = Integer.parseInt(token);
            if (integer < 0) {
                throw new RuntimeException("not positive number");
            }
            return integer;
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid number format");
        }
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream().reduce(0, Integer::sum);
    }
}
