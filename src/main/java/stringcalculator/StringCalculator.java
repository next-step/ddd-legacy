package stringcalculator;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StringCalculator {

    private static final String SEPARATOR = ",";

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        List<Integer> datas = convertToIntArray(text);
        return sum(datas);
    }

    private List<Integer> convertToIntArray(String text) {
        String[] textArray = split(text);
        return Arrays.stream(textArray)
                .map(Integer::parseInt)
                .collect(toList());
    }

    private int sum(List<Integer> datas) {
        return datas.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    private String[] split(String text) {
        return text.split(SEPARATOR);
    }
}
