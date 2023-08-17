package calculator.fixture;

import calculator.domain.PositiveStringNumber;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveStringNumbrersFixture {

    private PositiveStringNumbrersFixture() {
    }

    public static List<PositiveStringNumber> create(String... values) {
        return Arrays.stream(values)
            .map(PositiveStringNumber::new)
            .collect(Collectors.toList());
    }
}
