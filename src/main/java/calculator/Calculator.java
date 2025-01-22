package calculator;

import io.micrometer.common.util.StringUtils;

public class Calculator {

    public int calculate(String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }
        return Integer.parseInt(input);
    }
}
