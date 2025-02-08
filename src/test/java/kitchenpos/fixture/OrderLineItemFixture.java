package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static kitchenpos.fixture.MenuFixture.DEFAULT_MENU_PRICE;
import static kitchenpos.fixture.MenuFixture.menu;

public class OrderLineItemFixture {

   public static final Long DEFAULT_ORDER_LINE_ITEM_QUANTITY = 1L;
   private static final AtomicLong atomicLong = new AtomicLong(1L);

   public static OrderLineItem orderLineItem(final Long seq, final Menu menu, final long quantity, final UUID menuId, final BigDecimal price) {
      OrderLineItem orderLineItem = new OrderLineItem();
      orderLineItem.setSeq(seq);
      orderLineItem.setMenu(menu);
      orderLineItem.setQuantity(quantity);
      orderLineItem.setMenuId(menuId);
      orderLineItem.setPrice(price);
      return orderLineItem;
   }

   public static OrderLineItem orderLineItem(final Menu menu) {
      return orderLineItem(seq(), menu, DEFAULT_ORDER_LINE_ITEM_QUANTITY, menu.getId(), menu.getPrice());
   }

   public static OrderLineItem orderLineItem() {
      Menu menu = menu();
      return orderLineItem(seq(), menu, DEFAULT_ORDER_LINE_ITEM_QUANTITY, menu.getId(), DEFAULT_MENU_PRICE);
   }

   public static Long seq() {
      return atomicLong.getAndIncrement();
   }

}
