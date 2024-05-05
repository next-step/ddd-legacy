package stringcalculator;

import java.util.List;

public record ValidatedNumbers(List<Integer> integers) {

    public static ValidatedNumbers of(List<Integer> integers) {
        return new ValidatedNumbers(integers);
    }

    public List<Integer> getIntegers() {
        return integers;
    }
}
