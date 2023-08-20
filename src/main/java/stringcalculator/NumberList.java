package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NumberList {
    private final List<Number> numberList;

    private NumberList(List<Number> numberList) {
        this.numberList = numberList;
    }

    public static NumberList of(String[] numbers) {
        List<Number> numbersList = createNumberList(numbers);
        return new NumberList(numbersList);
    }

    private static List<Number> createNumberList(String[] numbers) {
        return Arrays.stream(numbers)
                .map(NumberList::convertNumber)
                .collect(Collectors.toList());
    }

    private static Number convertNumber(String number) {
        return Number.of(Integer.parseInt(number));
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
