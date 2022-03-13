package kitchenpos.fixture.orderFixture;

import kitchenpos.domain.*;
import org.aspectj.weaver.ast.Or;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static kitchenpos.KitchenposFixture.ID;
import static kitchenpos.KitchenposFixture.order;
import static kitchenpos.fixture.MenuFixture.정상_메뉴_가격_만원;
import static kitchenpos.fixture.OrderLineItemFixture.정상_주문_아이템_리스트;
import static kitchenpos.fixture.OrderLineItemFixture.주문_아이템_리스트_수량_음수;
import static kitchenpos.fixture.OrderTableFixture.정상_오더_테이블;

public class OrderFixture {


  private static final String ADDRESS = "order address";

  private static Order 주문_생성() {
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

  public static Order 주문_생성_타입_입력_수량이_양수(OrderType type) {
    Order order = 주문_생성();
    order.setType(type);
    order.setOrderDateTime(LocalDateTime.now());
    order.setOrderLineItems(정상_주문_아이템_리스트());
    order.setStatus(OrderStatus.WAITING);
    return order;
  }


  public static Order 배달_타입_주문_생성() {
    Order order = 주문_생성();
    order.setType(OrderType.DELIVERY);
    order.setDeliveryAddress(ADDRESS);
    return order;
  }

  public static Order 배달_타입_주문_상태_준비중() {
    Order order = 주문_생성();
    order.setType(OrderType.DELIVERY);
    order.setStatus(OrderStatus.SERVED);
    order.setDeliveryAddress(ADDRESS);
    return order;
  }

  public static Order 배달_타입_주문_상태_배달중() {
    Order order = 주문_생성();
    order.setType(OrderType.DELIVERY);
    order.setStatus(OrderStatus.DELIVERING);
    order.setDeliveryAddress(ADDRESS);
    return order;
  }

  public static Order 배달_타입_주문_상태_배달완료() {
    Order order = 주문_생성();
    order.setType(OrderType.DELIVERY);
    order.setStatus(OrderStatus.DELIVERED);
    order.setDeliveryAddress(ADDRESS);
    return order;
  }

  public static Order 배달_타입_주문_생성_주소_입력(String address) {
    Order order = 주문_생성();
    order.setType(OrderType.DELIVERY);
    order.setDeliveryAddress(address);
    return order;
  }

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
    Order order = 주문_생성();
    order.setType(OrderType.EAT_IN);
    OrderTable orderTable = 정상_오더_테이블();
    orderTable.setEmpty(true);
    order.setOrderTableId(orderTable.getId());
    order.setOrderTable(orderTable);
    order.setStatus(OrderStatus.SERVED);
    return order;
  }

  public static Order 포장() {
    Order order = 주문_생성();
    order.setType(OrderType.TAKEOUT);
    return order;
  }

  public static Order 포장_상태_준비중() {
    Order order = 주문_생성();
    order.setType(OrderType.TAKEOUT);
    order.setStatus(OrderStatus.SERVED);
    return order;
  }

  public static List<Order> 주문_리스트() {
    return Collections.singletonList(주문_생성());
  }


}
