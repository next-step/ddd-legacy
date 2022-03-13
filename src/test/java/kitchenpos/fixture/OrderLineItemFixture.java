package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static kitchenpos.fixture.MenuFixture.정상_메뉴_가격_만원;

public class OrderLineItemFixture {
  private static final long PLUS_ONE = 1L;
  private static final long MINUS_ONE = -1L;
  private static final BigDecimal PRICE = BigDecimal.valueOf(10000L);

  private static OrderLineItem 주문_아이템() {
    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setPrice(PRICE);

    Menu menu = 정상_메뉴_가격_만원();
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setMenu(menu);
    return orderLineItem;
  }

  public static OrderLineItem 정상_주문_아이템() {
    OrderLineItem orderLineItem = 주문_아이템();
    orderLineItem.setQuantity(PLUS_ONE);

    return orderLineItem;
  }

  public static OrderLineItem 주문_아이템_수량_음수() {
    OrderLineItem orderLineItem = 주문_아이템();
    orderLineItem.setQuantity(MINUS_ONE);

    return orderLineItem;
  }

  public static List<OrderLineItem> 정상_주문_아이템_리스트() {
    return Collections.singletonList(정상_주문_아이템());
  }

  public static List<OrderLineItem> 주문_아이템_리스트_수량_음수() {
    return Collections.singletonList(주문_아이템_수량_음수());
  }

}
