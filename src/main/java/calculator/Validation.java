package calculator;

import org.thymeleaf.util.StringUtils;

public class Validation {

    // 빈칸또는 null여부 체크
    public boolean isNull(String input) {
        return StringUtils.isEmpty(input);
    }

    // 주어진 문자열이 숫자인지 체크
    public boolean isInt(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


}
