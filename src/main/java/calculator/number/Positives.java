package calculator.number;

import java.util.*;

public class Positives {

    private final List<Positive> positives;

    public static final Positives EMPTY = new Positives(Collections.emptyList());

    public Positives(List<Positive> positives) {
        this.positives = positives;
    }

    public Positives(Positive... positive) {
        this.positives = List.of(positive);
    }

    public int sum() {
        return this.positives.stream()
                .mapToInt(Positive::getIntValue)
                .sum();
    }

}
