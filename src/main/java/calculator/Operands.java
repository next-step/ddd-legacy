package calculator;

import java.util.List;

public class Operands {
    private List<Integer> numbers;

    public Operands() {}

    public Operands(List<Integer> numbers) {
        validNumber(numbers);
        this.numbers = numbers;
    }

    private static void validNumber(List<Integer> numbers) {
        for (int number : numbers) {
            if (number < 0) {
                throw new IllegalArgumentException("음수는 추가될 수 없습니다.");
            }
        }
    }

    public int addAll() {
        return this.numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
