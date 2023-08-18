package calculator.fixture;

import calculator.domain.PositiveStringNumber;
import calculator.domain.PositiveStringNumbers;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveStringNumbersFixture {

    private PositiveStringNumbersFixture() {
    }

    public static PositiveStringNumbers create(String... values) {
        List<PositiveStringNumber> positiveStringNumbers = Arrays.stream(values)
            .map(PositiveStringNumber::of)
            .collect(Collectors.toList());
        return new PositiveStringNumbers(positiveStringNumbers);
    }
}
