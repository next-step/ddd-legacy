package kitchenpos;

import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderStatus.COMPLETED;
import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;

public class TestConstant {
    public static final UUID 후라이드치킨_PRODUCT_UUID = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
    public static final String TEST_PRODUCT_NAME = "TEST치킨";
    public static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
    public static final BigDecimal BIG_DECIMAL_MINUS_ONE = BigDecimal.valueOf(-1);
    public static final UUID 후라이드치킨_MENU_UUID = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
    public static final String 후라이드치킨_MENU_NAME = "후라이드 치킨메뉴";
    public static final UUID 후라이드치킨_MENU_GROUP_UUID = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");
    public static final String 한마리메뉴_MENU_GROUP_NAME = "한마리메뉴";
    public static final String 비속어_NAME = "비속어";
    public static final UUID ORDER_UUID = UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8");
    public static final OrderType ORDER_TYPE_배달주문 = DELIVERY;
    public static final OrderType ORDER_TYPE_매장내식사주문 = EAT_IN;
    public static final OrderStatus ORDER_STATUS_주문대기 = WAITING;
    public static final OrderStatus ORDER_STATUS_주문수락 = ACCEPTED;
    public static final OrderStatus ORDER_STATUS_배달중 = DELIVERING;
    public static final OrderStatus ORDER_STATUS_배달완료 = DELIVERED;
    public static final OrderStatus ORDER_STATUS_제공완료 = SERVED;
    public static final OrderStatus ORDER_STATUS_주문완료 = COMPLETED;
    public static final LocalDateTime ORDER_DATE_TIME_주문요청시간 = LocalDateTime.now();
    public static final int MINUS_QUANTITY = -1;
    public static final int DEFAULT_QUANTITY = 1;
    public static final OrderType ORDER_TYPE_미선택 = null;
    public static final String ORDER_TABLE_NAME = "1번";
    public static final boolean TABLE_UNUSABLE = false;

}
