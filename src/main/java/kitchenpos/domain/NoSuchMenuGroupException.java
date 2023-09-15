package kitchenpos.domain;

import java.util.NoSuchElementException;
import java.util.UUID;

public class NoSuchMenuGroupException extends NoSuchElementException {
    private static final String MESSAGE = "해당 메뉴그룹이 존재하지 않습니다. MenuGroup id 값: [%s]";

    public NoSuchMenuGroupException(UUID menuGroupId) {
        super(String.format(MESSAGE, menuGroupId));
    }
}
