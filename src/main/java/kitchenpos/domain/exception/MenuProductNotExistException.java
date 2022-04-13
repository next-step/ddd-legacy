package kitchenpos.domain.exception;

public class MenuProductNotExistException extends IllegalArgumentException {

    public MenuProductNotExistException() {
        super("하나 이상의 메뉴 상품을 포함해야 합니다.");
    }
}
