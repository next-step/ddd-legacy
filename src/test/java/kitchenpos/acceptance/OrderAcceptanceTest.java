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

  private Menu 신메뉴;

  @BeforeEach
  void init() {
    추천메뉴 = MenuGroupSteps.createMenuGroup("추천메뉴").jsonPath().getUUID("id");
    강정치킨 = ProductSteps.createProduct("강정치킨", 17000).jsonPath().getUUID("id");
    신메뉴 = new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, List.of(new MenuProduct(2, 강정치킨)), 추천메뉴);
  }

  @DisplayName("주문 매장식사 등록")
  @Test
  void createOrderEatIn() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.EAT_IN, List.of(orderLineItem), orderTableResponse.jsonPath().getUUID("id"));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(orderResponse.jsonPath().getString("status")).isEqualTo("WAITING");
  }

  @DisplayName("주문 포장 등록")
  @Test
  void createOrderTakeOut() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(orderResponse.jsonPath().getString("status")).isEqualTo("WAITING");
  }

  @DisplayName("주문 배달 등록")
  @Test
  void createOrderDelivery() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(orderResponse.jsonPath().getString("status")).isEqualTo("WAITING");
  }

  @DisplayName("주문 타입 null 에러")
  @Test
  void orderTypeNull() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

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
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);
    MenuSteps.chageDisplayHide(menuResponse.jsonPath().getUUID("id"));

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("주문 배달 수락")
  @Test
  void orderDeliveryAccept() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("ACCEPTED");
  }

  @DisplayName("주문 매장식사 수락")
  @Test
  void orderEatInAccept() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.EAT_IN, List.of(orderLineItem), orderTableResponse.jsonPath().getUUID("id"));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("ACCEPTED");
  }

  @DisplayName("주문 포장 수락")
  @Test
  void orderTakeOutAccept() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("ACCEPTED");
  }

  @DisplayName("주문 배달 서빙")
  @Test
  void orderDeliveryServe() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderServe(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("SERVED");
  }

  @DisplayName("주문 매장식사 서빙")
  @Test
  void orderEatInServe() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.EAT_IN, List.of(orderLineItem), orderTableResponse.jsonPath().getUUID("id"));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderServe(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("SERVED");
  }

  @DisplayName("주문 포장 서빙")
  @Test
  void orderTakeOutServe() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderServe(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("SERVED");
  }

  @DisplayName("주문 배달 배달 시작")
  @Test
  void orderDeliveryStart() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderServe(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderDeliveryStart(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("DELIVERING");
  }

  @DisplayName("주문 배달 배달 완료")
  @Test
  void orderDeliveryComplete() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderServe(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderDeliveryStart(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderDeliveryComplete(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("DELIVERED");
  }

  @DisplayName("주문 완료(배달)")
  @Test
  void orderCompleteDelivery() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.DELIVERY, List.of(orderLineItem), "경기도 남양주시");
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderServe(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderDeliveryStart(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderDeliveryComplete(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.ordercomplete(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("COMPLETED");
  }

  @DisplayName("주문 완료(매장식사)")
  @Test
  void orderCompleteEatIn() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ExtractableResponse<Response> orderTableSitResponse = OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    assertThat(orderTableSitResponse.jsonPath().getBoolean("occupied")).isEqualTo(true);

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.EAT_IN, List.of(orderLineItem), orderTableResponse.jsonPath().getUUID("id"));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderServe(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.ordercomplete(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("COMPLETED");
    assertThat(result.jsonPath().getList("orderTable.occupied")).containsExactly(false);
  }

  @DisplayName("주문 완료(포장)")
  @Test
  void orderCompleteTakeOut() {
    ExtractableResponse<Response> orderTableResponse = OrderTableSteps.createOrderTable("1번");

    assertThat(orderTableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(orderTableResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> menuResponse = MenuSteps.createMenu(신메뉴);

    OrderLineItem orderLineItem = new OrderLineItem(menuResponse.jsonPath().getUUID("id"), BigDecimal.valueOf(19000), 2);

    Order order = new Order(OrderType.TAKEOUT, List.of(orderLineItem));
    ExtractableResponse<Response> orderResponse = OrderSteps.createOrder(order);

    assertThat(orderResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderSteps.orderAccept(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.orderServe(orderResponse.jsonPath().getUUID("id"));
    OrderSteps.ordercomplete(orderResponse.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderSteps.getOrder();

    assertThat(result.jsonPath().getList("status")).containsExactly("COMPLETED");
  }
}
