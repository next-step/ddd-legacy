package calculator;

/* 빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다. */
public class Calculator {

    public int add(String text) {
        if(text == null || "".equals(text) || " ".equals(text) ) {
            return 0;
        }
        return 1;
    }
}
