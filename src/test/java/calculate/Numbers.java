package calculate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
    private final List<Integer> numbers;

    public Numbers(String[] numbers) {
        this.numbers =  Arrays.stream(numbers)
                .map(this::parseNumber)
                .collect(Collectors.toList());
    }

    public int calculate() {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    private int parseNumber(String numberString) {
        int number = parseInt(numberString);
        if(number < 0) {
            throw new RuntimeException("숫자는 음수일 수 없습니다.");
        }
        return number;
    }

    private int parseInt(String numberString) {
        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            throw new RuntimeException("유효한 문자열이 아닙니다.");
        }
    }

}
