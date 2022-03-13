package calculator.separator;

import calculator.Numbers;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <pre>
 * calculator
 *      CustomSeparator
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-02 오전 2:45
 */

public class CustomSeparator implements Separator {

    public static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    @Override
    public String[] division(String text) {

        Matcher matcher = CUSTOM_PATTERN.matcher(text);
        matcher.find();

        String delimiter = matcher.group(1);
        return matcher.group(2).split(delimiter);
    }
}
