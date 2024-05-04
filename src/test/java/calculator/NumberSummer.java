package calculator;

import java.util.List;

public class NumberSummer {

    public int sum(List<Integer> numbers) {
        return numbers.stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
}
