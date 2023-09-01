package kitchenpos.exception;

public class OrderExceptionMessage {
    public static final String ORDER_TYPE_NULL = "주문유형이 null 이면 안됩니다.";
    public static final String ORDER_LINE_ITEM_EMPTY = "주문아이템이 존재하지 않습니다.";
    public static final String NOT_FOUND_MENU = "주문아이템의 메뉴가 존재하지 않습니다.";
    public static final String ORDER_LINE_ITEM_QUANTITY_NEGATIVE = "주문아이템의 수량이 0보다 작으면 안됩니다.";
    public static final String ORDER_LINE_ITEM_MENU_NOT_DISPLAY = "주문아이템의 메뉴가 비노출상태면 안됩니다.";
    public static final String NOT_EQUALS_PRICE = "메뉴 가격과 주문아이템의 가격이 다르면 안됩니다.";
    public static final String DELIVERY_ADDRESS_EMPTY = "배송지 주소가 존재하지 않습니다.";
    public static final String NOT_FOUND_ORDER_TABLE = "주문테이블이 존재하지 않습니다.";
    public static final String NOT_OCCUPIED_ORDER_TABLE = "주문테이블이 비어있지 않습니다.";
    public static final String NOT_FOUND_ORDER = "주문이 존재하지 않습니다.";
    public static final String ORDER_STATUS_NOT_WAITING = "주문상태가 대기중이 아닙니다.";
    public static final String ORDER_STATUS_NOT_ACCEPTED = "주문상태가 수락됨이 아닙니다.";
    public static final String ORDER_STATUS_NOT_SERVED = "주문상태가 제공됨이 아닙니다.";
    public static final String ORDER_STATUS_NOT_DELIVERING = "주문상태가 배달중이 아닙니다.";
    public static final String ORDER_STATUS_NOT_DELIVERED = "주문상태가 배달됨이 아닙니다.";
    public static final String ORDER_TYPE_NOT_DELIVERY = "주문유형이 배달이 아닙니다.";
}
