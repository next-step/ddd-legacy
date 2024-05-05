package stringcalculator;

import java.util.List;

public class ParsedNumbers {
    private List<Integer> integers;

    private ParsedNumbers(List<Integer> integers) {
        this.integers = integers;
    }

    public static ParsedNumbers of(List<Integer> integers) {
        return new ParsedNumbers(integers);
    }

    public List<Integer> getIntegers() {
        return integers;
    }
}
