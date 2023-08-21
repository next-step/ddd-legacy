package calculator;

import org.springframework.util.StringUtils;

public class ZeroStingConvertor implements StingConvertor {

    private static final String ZERO = "0";

    @Override
    public boolean isSupport(String text) {
        return !StringUtils.hasText(text);
    }

    @Override
    public PositiveNumbers calculate(String text) {
        return new PositiveNumbers(new PositiveNumber(ZERO));
    }
}
