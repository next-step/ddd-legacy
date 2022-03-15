package kitchenpos.fixture.orderFixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import static kitchenpos.fixture.OrderTableFixture.정상_오더_테이블;
import static kitchenpos.fixture.orderFixture.OrderFixture.주문_생성;

public class OrderTypeEatInFixture {

  public static Order 매장_내_식사_주문_생성() {
    Order order = 주문_생성();
    order.setType(OrderType.EAT_IN);
    OrderTable orderTable = 정상_오더_테이블();
    orderTable.setEmpty(true);
    order.setOrderTableId(orderTable.getId());
    order.setOrderTable(orderTable);
    return order;
  }

  public static Order 매장_내_식사_주문_생성_상태_준비중() {
    Order order = 매장_내_식사_주문_생성();
    order.setStatus(OrderStatus.SERVED);
    return order;
  }

  public static Order 매장_내_식사_타입_주문_상태_입력(OrderStatus status) {
    Order order = 매장_내_식사_주문_생성();
    order.setStatus(status);
    return order;
  }

  public static Order 매장_내_식사_주문_테이블_존재하지_않음() {
    Order order = 매장_내_식사_주문_생성();
    order.setStatus(OrderStatus.SERVED);
    order.getOrderTable().setEmpty(false);
    return order;
  }

}
