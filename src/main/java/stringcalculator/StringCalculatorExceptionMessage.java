package stringcalculator;

public enum StringCalculatorExceptionMessage {

    IS_NEGATIVE("양수를 입력해주세요.")
    , CANNOT_CALCULATE("계산할 수 없습니다. 형식에 맞춰 다시 입력해주세요.");
    private String message;

    StringCalculatorExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
