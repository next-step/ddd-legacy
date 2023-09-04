package kitchenpos.acceptance.acceptance_step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptance.KitchenridersClientDummy;
import kitchenpos.domain.*;
import kitchenpos.test_fixture.OrderLineItemTestFixture;
import kitchenpos.test_fixture.OrderTestFixture;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderStep {
    private OrderStep() {
    }

    public static Order 등록할_배달주문_정보_생성한다(Menu 등록된_메뉴) {
        OrderLineItem 등록할_주문_메뉴 = OrderLineItemTestFixture.create()
                .changeMenu(등록된_메뉴)
                .changePrice(등록된_메뉴.getPrice())
                .changeQuantity(1)
                .getOrderLineItem();
        return OrderTestFixture.create()
                .changeId(null)
                .changeType(OrderType.DELIVERY)
                .changeOrderTable(null)
                .changeDeliveryAddress("서울시 강남구")
                .changeStatus(null)
                .changeOrderLineItems(Collections.singletonList(등록할_주문_메뉴))
                .getOrder();
    }

    public static Order 등록할_매장주문_정보_생성한다(Menu 등록된_메뉴, OrderTable 등록된_주문_테이블) {
        OrderLineItem 등록할_주문_메뉴 = OrderLineItemTestFixture.create()
                .changeMenu(등록된_메뉴)
                .changePrice(등록된_메뉴.getPrice())
                .changeQuantity(1)
                .getOrderLineItem();
        return OrderTestFixture.create()
                .changeId(null)
                .changeType(OrderType.EAT_IN)
                .changeOrderTable(등록된_주문_테이블)
                .changeDeliveryAddress(null)
                .changeStatus(null)
                .changeOrderLineItems(Collections.singletonList(등록할_주문_메뉴))
                .getOrder();
    }

    public static Order 등록할_포장주문_정보_생성한다(Menu 등록된_메뉴) {
        OrderLineItem 등록할_주문_메뉴 = OrderLineItemTestFixture.create()
                .changeMenu(등록된_메뉴)
                .changePrice(등록된_메뉴.getPrice())
                .changeQuantity(1)
                .getOrderLineItem();
        return OrderTestFixture.create()
                .changeId(null)
                .changeType(OrderType.TAKEOUT)
                .changeOrderTable(null)
                .changeDeliveryAddress(null)
                .changeStatus(null)
                .changeOrderLineItems(Collections.singletonList(등록할_주문_메뉴))
                .getOrder();
    }

    public static Order 주문_유형이_없는_주문_정보를_생성한다(Menu 등록된_메뉴) {
        OrderLineItem 등록할_주문_메뉴 = OrderLineItemTestFixture.create()
                .changeMenu(등록된_메뉴)
                .changePrice(등록된_메뉴.getPrice())
                .changeQuantity(1)
                .getOrderLineItem();
        return OrderTestFixture.create()
                .changeId(null)
                .changeType(null)
                .changeOrderTable(null)
                .changeDeliveryAddress("서울시 강남구")
                .changeStatus(null)
                .changeOrderLineItems(Collections.singletonList(등록할_주문_메뉴))
                .getOrder();
    }

    public static Order 배달_주소가_없는_주문_정보를_생성한다(Menu 등록된_메뉴) {
        OrderLineItem 등록할_주문_메뉴 = OrderLineItemTestFixture.create()
                .changeMenu(등록된_메뉴)
                .changePrice(등록된_메뉴.getPrice())
                .changeQuantity(1)
                .getOrderLineItem();
        return OrderTestFixture.create()
                .changeId(null)
                .changeType(OrderType.DELIVERY)
                .changeOrderTable(null)
                .changeDeliveryAddress("")
                .changeStatus(null)
                .changeOrderLineItems(Collections.singletonList(등록할_주문_메뉴))
                .getOrder();
    }

    public static ExtractableResponse<Response> 주문을_등록한다(final Order order) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(order)
                .when().post("/api/orders")
                .then().log().all()
                .extract();
    }

    public static Order 주문_테이블에_주문을_등록한다(final Menu menu, final OrderTable orderTable) {
        OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                .changeMenu(menu)
                .changePrice(menu.getPrice())
                .getOrderLineItem();
        Order 대기상태의_매장_주문 = OrderTestFixture.create()
                .changeOrderLineItems(Collections.singletonList(orderLineItem))
                .changeOrderTable(orderTable)
                .changeOrderTableId(orderTable)
                .changeType(OrderType.EAT_IN)
                .changeStatus(OrderStatus.WAITING)
                .getOrder();
        return 주문을_등록한다(대기상태의_매장_주문).body().as(Order.class);
    }

    public static void 배달주문_등록에_성공했다(
            ExtractableResponse<Response> response,
            String expectedDeliveryAddress,
            List<OrderLineItem> expectedOrderLineItems
    ) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Order 등록된_주문 = response.body().as(Order.class);
        assertThat(response.header("Location")).isEqualTo("/api/orders/" + 등록된_주문.getId());
        assertThat(등록된_주문.getOrderTableId()).isEqualTo(null);
        assertThat(등록된_주문.getType()).isEqualTo(OrderType.DELIVERY);
        assertThat(등록된_주문.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(등록된_주문.getDeliveryAddress()).isEqualTo(expectedDeliveryAddress);
        assertThat(등록된_주문.getOrderLineItems())
                .extracting("menu.id")
                .containsAll(expectedOrderLineItems.stream().map(OrderLineItem::getMenuId).collect(Collectors.toList()));
    }

    public static void 포장주문_등록에_성공했다(
            ExtractableResponse<Response> response,
            List<OrderLineItem> expectedOrderLineItems
    ) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Order 등록된_주문 = response.body().as(Order.class);
        assertThat(response.header("Location")).isEqualTo("/api/orders/" + 등록된_주문.getId());
        assertThat(등록된_주문.getOrderTableId()).isEqualTo(null);
        assertThat(등록된_주문.getType()).isEqualTo(OrderType.TAKEOUT);
        assertThat(등록된_주문.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(등록된_주문.getOrderLineItems())
                .extracting("menu.id")
                .containsAll(expectedOrderLineItems.stream().map(OrderLineItem::getMenuId).collect(Collectors.toList()));
    }

    public static void 매장주문_등록에_성공했다(
            ExtractableResponse<Response> response,
            List<OrderLineItem> expectedOrderLineItems
    ) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Order 등록된_주문 = response.body().as(Order.class);
        assertThat(response.header("Location")).isEqualTo("/api/orders/" + 등록된_주문.getId());
        assertThat(등록된_주문.getOrderTableId()).isEqualTo(null);
        assertThat(등록된_주문.getType()).isEqualTo(OrderType.EAT_IN);
        assertThat(등록된_주문.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(등록된_주문.getOrderLineItems())
                .extracting("menu.id")
                .containsAll(expectedOrderLineItems.stream().map(OrderLineItem::getMenuId).collect(Collectors.toList()));
    }

    public static void 주문_유형을_입력하지_않아서_주문_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 주문_메뉴를_입력하지_않아서_주문_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 존재하지_않는_메뉴를_주문에_포함시켜_주문_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 배달_주소를_입력하지_않아서_주문_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 주문_테이블이_비어있는_상태라서_매장_주문_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 주문_테이블이_존재하지_않는_주문_테이블이라서_매장_주문_등록에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static Order 배달주문이_등록된_상태다(Menu 메뉴) {
        Order 등록할_배달주문_정보 = 등록할_배달주문_정보_생성한다(메뉴);
        return 주문을_등록한다(등록할_배달주문_정보).body().as(Order.class);
    }

    public static Order 매장주문이_등록된_상태다(Menu 메뉴, OrderTable 주문_테이블) {
        Order 등록할_매장주문_정보 = 등록할_매장주문_정보_생성한다(메뉴, 주문_테이블);
        return 주문을_등록한다(등록할_매장주문_정보).body().as(Order.class);
    }

    public static Order 포장주문이_등록된_상태다(Menu 메뉴) {
        Order 등록할_포장주문_정보 = 등록할_포장주문_정보_생성한다(메뉴);
        return 주문을_등록한다(등록할_포장주문_정보).body().as(Order.class);
    }

    public static ExtractableResponse<Response> 주문을_수락_상태로_변경한다(Order order) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/" + order.getId() + "/accept")
                .then().log().all()
                .extract();
    }

    public static void 배달기사_회사에_배달요청을_했다(KitchenridersClientDummy kitchenridersClient) {
        assertThat(kitchenridersClient.getRequestDeliveryCallCount()).isOne();
    }

    public static void 대기중인_주문을_수락_상태로_변경했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Order 수락된_주문 = response.body().as(Order.class);
        assertThat(수락된_주문.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    public static void 대기_상태의_주문이_아니라서_주문_수락_상태_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 주문을_제공_상태로_변경한다(Order order) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/" + order.getId() + "/serve")
                .then().log().all()
                .extract();
    }

    public static void 수락_상태인_주문을_제공_상태로_변경에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Order 제공된_주문 = response.body().as(Order.class);
        assertThat(제공된_주문.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    public static void 수락_상태의_주문이_아니라서_제공_상태로_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 배달주문을_배달시작_상태로_변경한다(Order order) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/" + order.getId() + "/start-delivery")
                .then().log().all()
                .extract();
    }

    public static void 제공_상태인_주문을_배달시작_상태로_변경에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Order 배달시작된_주문 = response.body().as(Order.class);
        assertThat(배달시작된_주문.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    public static void 제공_상태가_아닌_주문이라서_배달시작_상태로_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 배달주문을_배달완료_상태로_변경한다(Order order) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/" + order.getId() + "/complete-delivery")
                .then().log().all()
                .extract();
    }

    public static void 배달시작_상태인_주문을_배달완료_상태로_변경에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Order 배달완료된_주문 = response.body().as(Order.class);
        assertThat(배달완료된_주문.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    public static void 배달시작_상태가_아닌_주문이라서_배달완료_상태로_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 주문을_주문완료_상태로_변경한다(Order order) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/" + order.getId() + "/complete")
                .then().log().all()
                .extract();
    }

    public static void 배달완료_상태인_배달주문을_주문완료_상태로_변경에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Order 주문완료된_주문 = response.body().as(Order.class);
        assertThat(주문완료된_주문.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    public static void 배달완료_상태가_아닌_배달주문이라서_주문완료_상태로_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 제공_상태가_아닌_매장주문이라서_주문완료_상태로_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 제공_상태인_매장주문을_주문완료_상태로_변경하고_주문테이블을_사용하지_않음_상태로_변경하는데_성공했다(
            ExtractableResponse<Response> response
    ) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Order 주문완료된_주문 = response.body().as(Order.class);
        assertThat(주문완료된_주문.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(주문완료된_주문.getOrderTable().isOccupied()).isFalse();
        assertThat(주문완료된_주문.getOrderTable().getNumberOfGuests()).isZero();
    }

    public static void 제공_상태인_포장주문을_주문완료_상태로_변경에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Order 주문완료된_주문 = response.body().as(Order.class);
        assertThat(주문완료된_주문.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    public static void 제공_상태가_아닌_포장주문이라서_주문완료_상태로_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
