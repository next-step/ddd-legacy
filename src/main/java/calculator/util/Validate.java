package calculator.util;

import java.util.Objects;

public final class Validate {

    public static boolean isNullOrBlank(final String input) {
        return Objects.isNull(input) || input.isBlank();
    }

}
