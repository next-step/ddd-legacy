package kitchenpos.application.Exception;

public enum ErrorCode {
    // 주문 관련 에러 (1000번대)
    ORDER_NOT_FOUND(1000, "주문을 찾을 수 없습니다"),
    ORDER_LINE_ITEM_NOT_FOUND(1001, "주문 항목을 찾을 수 없습니다"),
    ORDER_NOT_DELIVERED(1002, "아직 배달되지 않은 주문입니다"),
    ORDER_NOT_SERVED(1003, "아직 서빙되지 않은 주문입니다"),
    ORDER_LINE_QUANTITY_NEGATIVE(1004, "주문 수량은 음수일 수 없습니다"),
    ORDER_TYPE_INVALID(1005, "주문 타입이 유효하지 않습니다"),
    ORDER_LINE_ITEM_SIZE_NOT_MATCHED(1006, "주문 항목의 크기가 일치하지 않습니다"),
    ORDER_NOT_DELIVERED_YET(1007, "아직 배달되지 않은 주문입니다"),
    ORDER_NOT_SERVED_YET(1008, "아직 서빙되지 않은 주문입니다"),
    ORDER_STATUS_INVALID(1009, "주문 상태가 유효하지 않습니다"),


    // 메뉴 관련 에러 (2000번대)
    MENU_GROUP_NOT_FOUND(2000, "메뉴 그룹을 찾을 수 없습니다"),
    MENU_NAME_INVALID(2001, "메뉴 이름이 유효하지 않습니다"),
    MENU_PRICE_INVALID(2002, "메뉴 가격이 유효하지 않습니다"),
    MENU_PRODUCTS_EMPTY(2003, "메뉴 상품 목록이 비어있습니다"),
    MENU_DISPLAY_FALSE(2004, "비활성화된 메뉴입니다"),
    MENU_PRODUCTS_SIZE_NOT_MATCHED(2005, "메뉴 상품 목록의 크기가 일치하지 않습니다"),
    MENU_QUANTITY_NEGATIVE(2006, "메뉴 수량은 음수일 수 없습니다"),
    MENU_NOT_FOUND(2007, "메뉴를 찾을 수 없습니다"),

    // 배달 관련 에러 (3000번대)
    DELIVERY_ADDRESS_NOT_FOUND(3000, "배달 주소를 찾을 수 없습니다"),

    // 주문 테이블 관련 에러 (4000번대)
    ORDER_TABLE_NOT_FOUND(4000, "주문 테이블을 찾을 수 없습니다"),
    ORDER_TABLE_EMPTY(4001, "주문 테이블이 비어있습니다"),
    ORDER_TABLE_OCCUPIED(4002, "주문 테이블이 이미 점유되어 있습니다");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}