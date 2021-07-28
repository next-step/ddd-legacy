package calculator;

import java.util.Arrays;
import org.thymeleaf.util.StringUtils;

public class StringCalculator {

    public static final String DELIMITER = ",|:";

    public int calculate(final String stringNumber) {
        if (StringUtils.isEmpty(stringNumber)) {
            return 0;
        }
        return Arrays.stream(stringNumber.split(DELIMITER))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}
