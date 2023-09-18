package kitchenpos.domain;

public class InvalidNumberOfGuestsException extends IllegalArgumentException {
    private static final String MESSAGE = "방문한 손님 수는 음수일 수 없습니다. 현재 값: [%s]";

    public InvalidNumberOfGuestsException(int numberOfGuests) {
        super(String.format(MESSAGE, numberOfGuests));
    }
}
