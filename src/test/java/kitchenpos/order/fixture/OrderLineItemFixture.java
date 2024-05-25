package kitchenpos.order.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

import static kitchenpos.menu.fixture.MenuFixture.김치찜_1인_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.봉골레_파스타_세트_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.토마토_파스타_단품_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.피클_메뉴_숨김;


public class OrderLineItemFixture {

    public static final OrderLineItem 김치찜_1인_메뉴_1개_주문 = 주문_항목을_생성한다(김치찜_1인_메뉴, 1);

    public static final OrderLineItem 봉골레_파스타_세트_메뉴_1개_주문 = 주문_항목을_생성한다(봉골레_파스타_세트_메뉴, 1);

    public static final OrderLineItem 토마토_파스타_단품_메뉴_1개_주문 = 주문_항목을_생성한다(토마토_파스타_단품_메뉴, 1);

    public static final OrderLineItem 봉골레_파스타_세트_메뉴_마이너스_1개_주문 = 주문_항목을_생성한다(봉골레_파스타_세트_메뉴, -1);

    public static final OrderLineItem 숨김처리된_메뉴_1개_주문 = 주문_항목을_생성한다(피클_메뉴_숨김, 1);

    public static final OrderLineItem 주문_항목_가격_메뉴_가격_불일치 = 주문_항목을_생성한다(김치찜_1인_메뉴, 1, new BigDecimal(30_000));

    private static OrderLineItem 주문_항목을_생성한다(Menu menu, long quantity) {
        return 주문_항목을_생성한다(menu, quantity, menu.getPrice());
    }

    private static OrderLineItem 주문_항목을_생성한다(Menu menu, long quantity, BigDecimal price) {
        var 주문항목 = new OrderLineItem();
        주문항목.setMenuId(menu.getId());
        주문항목.setMenu(menu);
        주문항목.setQuantity(quantity);
        주문항목.setPrice(price);

        return 주문항목;
    }

}
