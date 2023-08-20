package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SplitStingConvertor implements StingConvertor {

    private static final String SEPARATOR = ",|:";

    @Override
    public boolean isSupport(String text) {
        return text.contains(",") || text.contains(":");
    }

    @Override
    public PositiveNumbers calculate(String text) {
        List<PositiveNumber> positiveNumbers = Arrays.stream(text.split(SEPARATOR))
                .map(PositiveNumber::new)
                .collect(Collectors.toList());

        return new PositiveNumbers(positiveNumbers);
    }
}
