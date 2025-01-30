package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delimiter {

    private static String basicDelimiter = ",:";
    private static String customPattern = "//(.*?)\\n";
    private static Pattern regex = Pattern.compile(customPattern);

    /* split : 구분자를 기준으로 문자열 분리 */
    public static String[] split(String input) {

        String custom = Delimiter.customise(input);
        if (custom != null) {
            input = input.replaceAll(customPattern, "");
        }

        return input.split("[" + basicDelimiter + custom + "]");
    }

    /* customise : 커스텀 구분자 찾기 */
    public static String customise(String input) {
        Matcher matcher = regex.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
