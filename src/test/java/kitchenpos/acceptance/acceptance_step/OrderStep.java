package kitchenpos.acceptance.acceptance_step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.*;
import kitchenpos.test_fixture.OrderLineItemTestFixture;
import kitchenpos.test_fixture.OrderTestFixture;
import org.springframework.http.MediaType;

import java.util.Collections;

import static io.restassured.RestAssured.*;

public class OrderStep {
    private OrderStep() {
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
}
