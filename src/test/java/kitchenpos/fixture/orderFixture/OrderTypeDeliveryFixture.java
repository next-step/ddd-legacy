package kitchenpos.fixture.orderFixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import static kitchenpos.fixture.orderFixture.OrderFixture.주문_생성;

public class OrderTypeDeliveryFixture {

  private static final String ADDRESS = "order address";

  public static Order 배달_타입_주문_생성() {
    Order order = 주문_생성();
    order.setType(OrderType.DELIVERY);
    order.setDeliveryAddress(ADDRESS);
    return order;
  }

  public static Order 배달_타입_주문_상태_준비중() {
    Order order = 배달_타입_주문_생성();
    order.setStatus(OrderStatus.SERVED);
    return order;
  }

  public static Order 배달_타입_주문_상태_배달중() {
    Order order = 배달_타입_주문_생성();
    order.setStatus(OrderStatus.DELIVERING);
    return order;
  }

  public static Order 배달_타입_주문_상태_배달완료() {
    Order order = 배달_타입_주문_생성();
    order.setStatus(OrderStatus.DELIVERED);
    return order;
  }

  public static Order 배달_타입_주문_생성_주소_입력(String address) {
    Order order = 배달_타입_주문_생성();
    order.setDeliveryAddress(address);
    return order;
  }

  public static Order 배달_타입_주문_상태_입력(OrderStatus status) {
    Order order = 배달_타입_주문_생성();
    order.setStatus(status);
    return order;
  }

}
