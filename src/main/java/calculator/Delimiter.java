package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delimiter {

    private static final String BASIC_DELIMITER = ",:";
    private static final String customPattern = "//(.*?)\\n";
    private static Pattern regex = Pattern.compile(customPattern);

    private Delimiter() {
    }

    /* split : 구분자를 기준으로 문자열 분리 */
    public static List<String> split(String input) {

        String custom = Delimiter.customize(input);
        if (custom != null) {
            input = input.replaceAll(customPattern, "");
        }

        return List.of(input.split("[" + BASIC_DELIMITER + custom + "]"));
    }

    /* customise : 커스텀 구분자 찾기 */
    public static String customize(String input) {
        Matcher matcher = regex.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
