package calculator;

import java.util.Arrays;

public class Numbers {

    private final PositiveNumber[] positiveNumbers;


    public Numbers(String[] positiveNumbers) {
        this.positiveNumbers = Arrays.stream(positiveNumbers)
                .map(PositiveNumber::valueOf)
                .toArray(PositiveNumber[]::new);
    }

    public int sum() {
        return positiveNumbers.length == 0 ? 0 : Arrays.stream(positiveNumbers)
                .mapToInt(PositiveNumber::getValue)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Numbers other = (Numbers) o;
        return Arrays.equals(positiveNumbers, other.positiveNumbers);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(positiveNumbers);
    }

}
