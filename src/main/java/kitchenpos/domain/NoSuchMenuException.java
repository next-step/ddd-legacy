package kitchenpos.domain;

import java.util.NoSuchElementException;
import java.util.UUID;

public class NoSuchMenuException extends NoSuchElementException {
    private static final String MESSAGE = "해당 메뉴가 존재하지 않습니다. Menu id 값: [%s]";

    public NoSuchMenuException(UUID menuId) {
        super(String.format(MESSAGE, menuId));
    }
}
