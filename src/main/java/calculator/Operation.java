package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operation {
    // 구분자를 "|"로 연결
    String operator = ",|:";

    public String sepOperatorFromText(String input){
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(input);
        if (m.find()) {
            operator = operator + "|" + m.group(1);
            return m.group(2);
        }
        return input;
    }


    public int addition(String [] intList){
        Validation valid = new Validation();

        int total = 0;

        for(String strInt:intList){
            if(valid.isNull(strInt)){  // 값이 빈 경우 0으로 처리하며 다음 숫자로 넘어감.
                continue;
            }
            if(!valid.isInt(strInt)){  // 숫자가 아닌 입력인 경우 에러처리
                throw new RuntimeException("입력문자에 숫자가 아닌 값이 포함되어있습니다: "+strInt);
            }
            if(Integer.parseInt(strInt)<0){  // 음수인 경우 에러처리
                throw new RuntimeException("입력문자에 음수가 포함되어있습니다: "+strInt);
            }
            total = total + Integer.parseInt(strInt);
        }

        return total;
    }

    public String getOperator() {
        return operator;
    }
}
