package kitchenpos.domain.exception;

public class MenuPriceException extends IllegalArgumentException {

    public MenuPriceException() {
        super("메뉴의 가격은 0원 이상이어야 합니다.");
    }

}
