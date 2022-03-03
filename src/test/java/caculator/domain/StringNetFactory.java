package caculator.domain;

import java.util.Objects;

public class StringNetFactory {

    private static final String CUSTOM_DELIMITER_PREFIX = "//";

    public static StringNet getStringNet(String input) {
        if (hasCustomPrefix(input)) {
            return new CustomStringNet();
        }
        return new DefaultStringNet();
    }

    private static boolean hasCustomPrefix(String input) {
        return !Objects.isNull(input) && input.startsWith(CUSTOM_DELIMITER_PREFIX);
    }

}
