package caculator.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Numbers {

    private final List<StringNumber> stringNumbers;

    public Numbers(List<StringNumber> stringNumbers) {
        this.stringNumbers = stringNumbers;
    }

    public static Numbers from(String[] stringNumbers) {
        return new Numbers(Arrays.stream(stringNumbers)
            .map(StringNumber::valueOf)
            .collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Numbers numbers = (Numbers) o;
        return Objects.equals(stringNumbers, numbers.stringNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringNumbers);
    }
}
