package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("주문")
public class OrderAcceptanceTest extends AcceptanceTest {

  private UUID 추천메뉴;
  private UUID 강정치킨;
  private UUID 테이블_1번;
  private UUID 신메뉴;

  private OrderLineItem orderLineItem;

  @BeforeEach
  void init() {
    추천메뉴 = MenuGroupSteps.createMenuGroup("추천메뉴").jsonPath().getUUID("id");
    강정치킨 = ProductSteps.createProduct("강정치킨", 17000).jsonPath().getUUID("id");
    테이블_1번 = OrderTableSteps.createOrderTable("1번").jsonPath().getUUID("id");
    Menu menu = new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, List.of(new MenuProduct(2, 강정치킨)), 추천메뉴);
    신메뉴 = MenuSteps.createMenu(menu).jsonPath().getUUID("id");

    orderLineItem = new OrderLineItem(신메뉴, BigDecimal.valueOf(19000), 2);
  }

  @DisplayName("주문 매장식사 등록")
  @Test
  void createOrderEatIn() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.EAT_IN, List.of(orderLineItem), 테이블_1번);

    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(orderResponse.jsonPath().getString("status")).isEqualTo("WAITING");
  }

  @DisplayName("주문 포장 등록")
  @Test
  void createOrderTakeOut() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(orderResponse.jsonPath().getString("status")).isEqualTo("WAITING");
  }

  @DisplayName("주문 배달 등록")
  @Test
  void createOrderDelivery() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(orderResponse.jsonPath().getString("status")).isEqualTo("WAITING");
  }

  @DisplayName("주문 타입 null 에러")
  @Test
  void orderTypeNull() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(null, List.of(orderLineItem));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("주문 아이템 null 에러")
  @Test
  void orderLineItemNull() {
    Order order = new Order(OrderType.TAKEOUT, null);
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("노출되지 않은 메뉴는 선택하면 에러")
  @Test
  void menuDisplayHideSelect() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);
    MenuSteps.chageDisplayHide(신메뉴);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("주문 배달 수락")
  @Test
  void orderDeliveryAccept() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    UUID 배달주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderAccept(배달주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("ACCEPTED");
  }

  @DisplayName("주문 매장식사 수락")
  @Test
  void orderEatInAccept() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.EAT_IN, List.of(orderLineItem), 테이블_1번);
    UUID 매장식사주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderAccept(매장식사주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("ACCEPTED");
  }

  @DisplayName("주문 포장 수락")
  @Test
  void orderTakeOutAccept() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    UUID 포장주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderAccept(포장주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("ACCEPTED");
  }

  @DisplayName("주문 배달 서빙")
  @Test
  void orderDeliveryServe() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    UUID 배달주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderAcceptToServe(배달주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("SERVED");
  }

  @DisplayName("주문 매장식사 서빙")
  @Test
  void orderEatInServe() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.EAT_IN, List.of(orderLineItem), 테이블_1번);
    UUID 매장식사주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderAcceptToServe(매장식사주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("SERVED");
  }

  @DisplayName("주문 포장 서빙")
  @Test
  void orderTakeOutServe() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    UUID 포장주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderAcceptToServe(포장주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("SERVED");
  }

  @DisplayName("주문 배달 배달 시작")
  @Test
  void orderDeliveryStart() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    UUID 배달주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderAcceptToDeliveryStart(배달주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("DELIVERING");
  }

  @DisplayName("주문 배달 배달 완료")
  @Test
  void orderDeliveryComplete() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    UUID 배달주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderAcceptToDeliveryComplete(배달주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("DELIVERED");
  }

  @DisplayName("주문 완료(배달)")
  @Test
  void orderCompleteDelivery() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    UUID 배달주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderDeliveryAcceptToComplete(배달주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("COMPLETED");
  }

  @DisplayName("주문 완료(매장식사)")
  @Test
  void orderCompleteEatIn() {
    ExtractableResponse<Response> orderTableSitResponse = OrderTableSteps.chageOrderTableSit(테이블_1번);

    assertThat(orderTableSitResponse.jsonPath().getBoolean("occupied")).isEqualTo(true);

    Order order = new Order(OrderType.EAT_IN, List.of(orderLineItem), 테이블_1번);
    UUID 매장식사주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderNotDeliveryAcceptToComplete(매장식사주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("COMPLETED");
    assertThat(result.jsonPath().getList("orderTable.occupied")).containsExactly(false);
  }

  @DisplayName("주문 완료(포장)")
  @Test
  void orderCompleteTakeOut() {
    OrderTableSteps.chageOrderTableSit(테이블_1번);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    UUID 포장주문 = OrderSteps.createOrder(order).jsonPath().getUUID("id");

    orderNotDeliveryAcceptToComplete(포장주문);

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("COMPLETED");
  }

  private void orderNotDeliveryAcceptToComplete(UUID orderId) {
    orderAcceptToServe(orderId);
    OrderSteps.ordercomplete(orderId);
  }

  private void orderDeliveryAcceptToComplete(UUID orderId) {
    orderAcceptToDeliveryComplete(orderId);
    OrderSteps.ordercomplete(orderId);
  }

  private void orderAcceptToDeliveryComplete(UUID orderId) {
    orderAcceptToDeliveryStart(orderId);
    OrderSteps.orderDeliveryComplete(orderId);
  }

  private void orderAcceptToDeliveryStart(UUID orderId) {
    orderAcceptToServe(orderId);
    OrderSteps.orderDeliveryStart(orderId);
  }

  private void orderAcceptToServe(UUID orderId) {
    orderAccept(orderId);
    OrderSteps.orderServe(orderId);
  }

  private void orderAccept(UUID orderId) {
    OrderSteps.orderAccept(orderId);
  }
}
