package springcalculator;

import java.util.ArrayList;
import java.util.List;

public class StringCalculator {
    private final List<Integer> numbers;

    public StringCalculator(String input) {
        this.numbers = new ArrayList<>();
        parseInput(input);
    }

    public int add() {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    private void parseInput(String input) {
        if (input == null || input.isEmpty()) {
            return;
        }

        String[] tokens = new Delimiter(input).extractNumbers();
        for (String token : tokens) {
            try {
                int number = Integer.parseInt(token);
                if (number < 0) {
                    throw new RuntimeException("음수값을 넣을 수 없습니다.");
                }
                numbers.add(number);
            } catch (NumberFormatException e) {
                throw new RuntimeException("숫자 이외의 값을 들어갈 수 없습니다.");
            }
        }
    }
}
