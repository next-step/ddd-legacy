package calculator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operation {
    // 구분자를 "|"로 연결
    String operator = ",|:";

    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");


    public String sepOperatorFromText(String input) {
        Matcher m = PATTERN.matcher(input);
        if (m.find()) {
            operator = operator + "|" + m.group(1);
            return m.group(2);
        }
        return input;
    }

    public List<Integer> strListToIntList(String[] intList) {
        Validation valid = new Validation();
        List<Integer> strToInt = new ArrayList<>();
        for (String strInt : intList) {
            if (valid.isNull(strInt)) {  // 값이 빈 경우 0으로 처리하며 다음 숫자로 넘어감.
                strInt = "0";
            }
            PositiveInteger poInt = new PositiveInteger(strInt);
            strToInt.add(Integer.parseInt(strInt));
        }
        return strToInt;
    }


    public int addition(List<Integer> intList) {
        return intList.stream().mapToInt(Integer::intValue).sum();
    }

    public String getOperator() {
        return operator;
    }
}
