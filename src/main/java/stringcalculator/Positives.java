package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Positives {
    private final List<Positive> positives;

    public Positives(String[] stringNumbers) {
        this.positives = Arrays
                .stream(stringNumbers)
                .map(Positive::new)
                .collect(Collectors.toList());
    }

    public int getSum() {
        return positives.stream().mapToInt(Positive::parseInt).sum();
    }
}
