package calculator.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Positives {
    private final List<Positive> positives;

    public Positives(List<Integer> numbers) {
        this.positives = numbers.stream().map(Positive::new).collect(Collectors.toList());
    }

    public int sum() {
        return positives.stream().mapToInt(Positive::getPositive).sum();
    }
}
