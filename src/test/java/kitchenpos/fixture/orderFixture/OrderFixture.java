package kitchenpos.fixture.orderFixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static kitchenpos.KitchenposFixture.ID;
import static kitchenpos.fixture.OrderLineItemFixture.정상_주문_아이템_리스트;
import static kitchenpos.fixture.OrderLineItemFixture.주문_아이템_리스트_수량_음수;

public class OrderFixture {

  static Order 주문_생성() {
    Order order = new Order();
    order.setId(ID);
    order.setOrderDateTime(LocalDateTime.now());
    order.setOrderLineItems(정상_주문_아이템_리스트());
    order.setStatus(OrderStatus.WAITING);
    return order;
  }

  public static Order 주문_생성_타입_입력(OrderType type) {
    Order order = 주문_생성();
    order.setType(type);
    return order;
  }

  public static Order 주문_생성_상태_입력(OrderStatus status) {
    Order order = 주문_생성();
    order.setStatus(status);
    return order;
  }

  public static Order 주문_생성_타입_입력_수량이_음수(OrderType type) {
    Order order = 주문_생성();
    order.setType(type);
    order.setOrderDateTime(LocalDateTime.now());
    order.setOrderLineItems(주문_아이템_리스트_수량_음수());
    order.setStatus(OrderStatus.WAITING);
    return order;
  }

  public static List<Order> 주문_리스트() {
    return Collections.singletonList(주문_생성());
  }
}
