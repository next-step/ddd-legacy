package utils;

import java.util.*;

public class StringUtils {

    public static boolean isBlankWhenTrim(String text) {
        return Objects.isNull(text) || text.trim().isEmpty();
    }

}
