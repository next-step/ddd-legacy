package utils;

import java.util.*;

public class StringUtils {

    public static boolean isBlank(String text) {
        return Objects.isNull(text) || text.isEmpty();
    }

}
