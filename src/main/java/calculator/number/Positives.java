package calculator.number;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class Positives {

    private final List<Positive> positives;

    public static final Positives EMPTY = new Positives(Collections.emptyList());

    public Positives(String[] texts) {
        this(of(texts));
    }

    public Positives(List<Positive> positives) {
        this.positives = positives;
    }

    public Positive sum() {
        return this.positives.stream()
                .reduce(Positive.ZERO, Positive::add);
    }

    public static List<Positive> of(String[] texts) {
        return Arrays.stream(texts)
                .map(Positive::new)
                .toList();
    }

}
