package calculator;

import org.springframework.util.StringUtils;

public class StringCalculator {

    public int add(String text) {
        if (!StringUtils.hasLength(text)) {
            return 0;
        }

        return Integer.parseInt(text);
    }

}
