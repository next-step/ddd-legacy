package kitchenpos.application.constant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class KitchenposTestConst {

    public static final LocalDateTime TEST_ORDER_DATE_TIME
        = LocalDateTime.of(2023, 9, 5, 10, 0, 0);

    public static final String TEST_ORDER_TABLE_NAME = "매장테이블1번";
    public static final int TEST_ORDER_TABLE_NUMBER_OF_GUEST = 3;

    public static final String TEST_DELIVERY_ADDRESS = "경기도 성남시 분당구";

    public static final String TEST_MENU_NAME = "후라이드 치킨 두마리";

    public static final BigDecimal TEST_MENU_PRICE = BigDecimal.valueOf(1_000L);

    public static final String TEST_MENU_GROUP_NAME = "두마리 세트";

    public static final String TEST_PRODUCT_NAME = "후라이드 치킨";
    public static final BigDecimal TEST_PRODUCT_PRICE = BigDecimal.valueOf(2_000L);

    public static final BigDecimal TEST_ORDER_LINE_PRICE = BigDecimal.valueOf(3_000L);

    private KitchenposTestConst() {
        throw new UnsupportedOperationException();
    }
}
