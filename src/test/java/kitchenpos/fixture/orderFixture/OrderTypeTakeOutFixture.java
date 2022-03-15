package kitchenpos.fixture.orderFixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import static kitchenpos.fixture.orderFixture.OrderFixture.주문_생성;

public class OrderTypeTakeOutFixture {
  private static Order 포장_타입_주문_생성() {
    Order order = 주문_생성();
    order.setType(OrderType.TAKEOUT);
    return order;
  }

  public static Order 포장_상태_준비중() {
    Order order = 포장_타입_주문_생성();
    order.setStatus(OrderStatus.SERVED);
    return order;
  }

  public static Order 포장_타입_주문_상태_입력(OrderStatus status) {
    Order order = 포장_타입_주문_생성();
    order.setStatus(status);
    return order;
  }

}
