package kitchenpos.exception;

public class MenuExceptionMessage {
    public static final String PRICE_MORE_ZERO = "메뉴의 가격은 0보다 큰 숫자여야 합니다.";
    public static final String NOT_EQUAL_MENU_PRODUCT_SIZE = "메뉴상품의 수와 등록된 상품의 수가 다릅니다.";
    public static final String ILLEGAL_QUANTITY = "메뉴상품의 수량은 0 이상이어야 합니다.";
    public static final String MENU_PRICE_MORE_PRODUCTS_SUM = "메뉴가격이 메뉴상품들 가격의 합보다 크면 안됩니다.";
    public static final String MENU_NAME_CONTAINS_PURGOMALUM = "메뉴 이름에 비속어가 포함되면 안됩니다.";
    public static final String NOT_FOUND_MENU_GROUP = "메뉴 그룹이 존재하지 않습니다.";
    public static final String EMPTY_MENU_PRODUCT = "메뉴상품목록이 존재하지 않습니다.";
    public static final String NOT_FOUND_PRODUCT = "상품이 존재하지 않습니다.";
    public static final String NOT_FOUND_MENU = "메뉴가 존재하지 않습니다.";
}
