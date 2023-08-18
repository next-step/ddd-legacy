package racingcar;

public enum RacingCarExceptionMessage {
    NAME_EMPTY("자동차 이름을 입력해주세요.")
    , NAME_BIGGER_THAN_FIVE("자동차 이름은 5글자 이하로 입력해주세요.")

    ;
    private String message;

    RacingCarExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
