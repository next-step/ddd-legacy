package calculator;

public class PositiveInteger {

    public PositiveInteger(String text) {
        Validation valid = new Validation();

        if (!valid.isInt(text)) {  // 숫자가 아닌 입력인 경우 에러처리
            throw new IllegalArgumentException("입력문자에 숫자가 아닌 값이 포함되어있습니다: " + text);
        }
        if (Integer.parseInt(text) < 0) {  // 음수인 경우 에러처리
            //throw new RuntimeException("입력문자에 음수가 포함되어있습니다: " + strInt);
            throw new IllegalArgumentException("입력문자에 음수가 포함되어있습니다: " + text);
        }
    }

}
