package calculator.separator;

import calculator.Numbers;

/**
 * <pre>
 * calculator
 *      Separator
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-02 오전 2:44
 */

@FunctionalInterface
public interface Separator {
    String[] division(String text);
}
