package calculate.domain;

import java.util.*;

public class NumberGroups {

    private final List<Number> numbers;

    public NumberGroups() {
        this(new ArrayList<>());
    }

    public NumberGroups(final String sentence) {
        this.numbers = NumberGroupsParser.parse(sentence);
    }

    public NumberGroups(final int...numbers) {
        this.numbers = Arrays.stream(numbers)
                .mapToObj(Number::new)
                .toList();
    }

    public NumberGroups(final List<Number> numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        return numbers.stream()
                .mapToInt(Number::getValue)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NumberGroups that = (NumberGroups) o;
        return Objects.equals(numbers, that.numbers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(numbers);
    }

    @Override
    public String toString() {
        return "NumberGroups{" +
                "numbers=" + numbers +
                '}';
    }

}
