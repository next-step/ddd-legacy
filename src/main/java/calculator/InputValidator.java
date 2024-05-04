package calculator;

import java.util.Objects;

public class InputValidator {

    public static boolean validation(String text) {
        return Objects.isNull(text) || text.isBlank();
    }
}
