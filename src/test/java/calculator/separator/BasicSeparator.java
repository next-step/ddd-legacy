package calculator.separator;

import calculator.Numbers;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * <pre>
 * calculator.separator
 *      BasicSeparator
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-02 오전 2:46
 */

public class BasicSeparator implements Separator{

    private static final String COMMA = ",";
    private static final String COLON = ":";

    @Override
    public String[] division(String text) {

        return text.split(separator(COMMA, COLON));
    }

    private String separator(String... separator) {

        return Arrays.stream(separator).collect(Collectors.joining("|"));
    }

}
