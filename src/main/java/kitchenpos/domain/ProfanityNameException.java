package kitchenpos.domain;

public class ProfanityNameException extends IllegalArgumentException {
    private static final String MESSAGE = "이름은 null이거나 욕설일 수 없습니다. 현재 값: [%s]";

    public ProfanityNameException(String name) {
        super(String.format(MESSAGE, name));
    }
}
