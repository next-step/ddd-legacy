package calculator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StringUtils {

    private StringUtils() {
    }

    public static String defaultString(String text) {
        if (text == null) {
            return "";
        }

        return text;
    }

    public static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    public static List<String> toList(String text, String delimiter) {
        if (isBlank(text)) {
            return new ArrayList<>();
        }

        return Stream.of(text.split(delimiter))
                .collect(Collectors.toList());
    }
}
