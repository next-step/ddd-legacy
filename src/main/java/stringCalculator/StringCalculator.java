package stringCalculator;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.List;

public class StringCalculator implements Calculator {

    private static final int ZERO = 0;

    private final StringPatternParse stringPatternParse;

    public StringCalculator(StringPatternParse stringPatternParse) {
        this.stringPatternParse = stringPatternParse;
    }

    @Override
    public int add(String text) {

        if (StringUtils.isBlank(text))
            return ZERO;

        List<PositiveNumber> listPositiveNumbers = stringPatternParse.parseStringPatternToPositiveNumberList(text);
        if (!listPositiveNumbers.isEmpty()) {
            return listPositiveNumbers.stream()
                    .map(pn -> pn.getPositiveNumber())
                    .reduce(0, Integer::sum);
        }

        return new PositiveNumber(text).getPositiveNumber();
    }

}
