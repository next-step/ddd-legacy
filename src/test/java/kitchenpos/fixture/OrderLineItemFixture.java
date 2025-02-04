package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static kitchenpos.fixture.MenuFixture.menu;

public class OrderLineItemFixture {

   public static final Long DEFAULT_MENU_QUANTITY = 1L;
   public static final BigDecimal DEFAULT_MENU_PRICE = new BigDecimal(10_000);
   private static final AtomicLong atomicLong = new AtomicLong(1);

   public static OrderLineItem orderLineItem(final Long seq, final Menu menu, final long quantity, final UUID menuId, final BigDecimal price) {
      OrderLineItem orderLineItem = new OrderLineItem();
      orderLineItem.setSeq(seq);
      orderLineItem.setMenu(menu);
      orderLineItem.setQuantity(quantity);
      orderLineItem.setMenuId(menuId);
      orderLineItem.setPrice(price);

      return orderLineItem;
   }

   public static OrderLineItem orderLineItem() {
      Menu menu = menu();
      return orderLineItem(seq(), menu, DEFAULT_MENU_QUANTITY, menu.getId(), DEFAULT_MENU_PRICE);
   }

   public static Long seq() {
      return atomicLong.getAndIncrement();
   }

}
