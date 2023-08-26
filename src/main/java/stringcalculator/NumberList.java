package stringcalculator;

import java.util.List;

public class NumberList {
    private final List<Number> numberList;

    private NumberList(List<Number> numberList) {
        this.numberList = numberList;
    }

    public static NumberList of(List<Number> numberList) {
        return new NumberList(numberList);
    }

    public int sum() {
        return numberList.stream()
                .mapToInt(Number::getValue)
                .sum();
    }

    public Number get(int index) {
        return numberList.get(index);
    }
}
