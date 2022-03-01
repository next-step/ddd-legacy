package calculator;

/* 숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다. */
public class Calculator {

    public int add(String text) {
        if(text == null || "".equals(text) || " ".equals(text) ) {
            return 0;
        }
        if(text.length()==1&&isNumeric(text)) {
            return Integer.parseInt(text);
        }
        return 1;
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Integer.parseInt(strNum);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
