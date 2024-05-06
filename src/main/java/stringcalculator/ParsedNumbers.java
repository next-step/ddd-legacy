package stringcalculator;

import java.util.List;

public record ParsedNumbers(List<Integer> integers) {

    public static ParsedNumbers of(List<Integer> integers) {
        return new ParsedNumbers(integers);
    }

    public List<Integer> getIntegers() {
        return integers;
    }
}
