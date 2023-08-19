package calculator;

import org.springframework.util.StringUtils;

public class ZeroCalculatorPolicy implements CalculatorPolicy {

    private static final int ZERO = 0;

    @Override
    public boolean isSupport(String text) {
        return !StringUtils.hasText(text);
    }

    @Override
    public int calculate(String text) {
        return ZERO;
    }
}
