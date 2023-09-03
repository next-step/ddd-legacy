package kitchenpos.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.*;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.ui.MenuGroupRestControllerTest.메뉴_그룹_생성_요청;
import static kitchenpos.ui.MenuRestControllerTest.메뉴_생성_요청;
import static kitchenpos.ui.OrderTableRestControllerTest.테이블_사용상태_변경_요청;
import static kitchenpos.ui.OrderTableRestControllerTest.테이블_생성_요청;
import static kitchenpos.ui.ProductRestControllerTest.상품_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("주문 API")
class OrderRestControllerTest extends ControllerTest {

    OrderLineItem orderLineItem;
    OrderTable orderTable;
    Product product;
    Menu menu;
    MenuGroup menuGroup;
    Order order;

    @DisplayName("배달 주문 API 테스트")
    @TestFactory
    Stream<DynamicNode> deliveryOrder() {
        return Stream.of(
                dynamicTest("데이터를 세팅한다.", () -> {
                    product = 상품_생성_요청("치킨", BigDecimal.valueOf(15_000L)).as(Product.class);
                    menuGroup = 메뉴_그룹_생성_요청("menuGroup1").as(MenuGroup.class);
                    menu = 메뉴_생성_요청("치킨", BigDecimal.valueOf(15_000L), menuGroup.getId(), product).as(Menu.class);
                    orderLineItem = new OrderLineItem();
                    orderLineItem.setSeq(1L);
                    orderLineItem.setMenuId(menu.getId());
                    orderLineItem.setQuantity(1);
                    orderLineItem.setPrice(BigDecimal.valueOf(15_000L));
                }),
                dynamicTest("배달 주문을 요청한다.", () -> {
                    ExtractableResponse<Response> response = 주문_요청(OrderType.DELIVERY, List.of(orderLineItem), "서울시 강남구", null);

                    주문_생성됨(response);
                    order = response.as(Order.class);
                }),
                dynamicTest("주문을 승인한다.", () -> {
                    ExtractableResponse<Response> response = 주문_승인_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                }),
                dynamicTest("주문을 배달기사에게 제공한다.", () -> {
                    ExtractableResponse<Response> response = 주문_제공_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                }),
                dynamicTest("주문 배달을 시작한다.", () -> {
                    ExtractableResponse<Response> response = 주문_배달_시작_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                }),
                dynamicTest("주문 배달은 완료한다.", () -> {
                    ExtractableResponse<Response> response = 주문_배달_완료_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                }),
                dynamicTest("주문을 완료한다.", () -> {
                    ExtractableResponse<Response> response = 주문_완료_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                })
        );
    }

    @DisplayName("포장 주문 API 테스트")
    @TestFactory
    Stream<DynamicNode> takeOutOrder() {
        return Stream.of(
                dynamicTest("데이터를 세팅한다.", () -> {
                    product = 상품_생성_요청("치킨", BigDecimal.valueOf(15_000L)).as(Product.class);
                    menuGroup = 메뉴_그룹_생성_요청("menuGroup1").as(MenuGroup.class);
                    menu = 메뉴_생성_요청("치킨", BigDecimal.valueOf(15_000L), menuGroup.getId(), product).as(Menu.class);
                    orderLineItem = new OrderLineItem();
                    orderLineItem.setSeq(1L);
                    orderLineItem.setMenuId(menu.getId());
                    orderLineItem.setQuantity(1);
                    orderLineItem.setPrice(BigDecimal.valueOf(15_000L));
                }),
                dynamicTest("포장 주문을 요청한다.", () -> {
                    ExtractableResponse<Response> response = 주문_요청(OrderType.TAKEOUT, List.of(orderLineItem), null, null);

                    주문_생성됨(response);
                    order = response.as(Order.class);
                }),
                dynamicTest("주문을 승인한다.", () -> {
                    ExtractableResponse<Response> response = 주문_승인_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                }),
                dynamicTest("주문을 제공한다.", () -> {
                    ExtractableResponse<Response> response = 주문_제공_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                }),
                dynamicTest("포장 주문은 배달 상태로 변경할 수 없다.", () -> {
                    ExtractableResponse<Response> response = 주문_배달_시작_요청(order.getId());

                    주문_상태_변경_실패됨(response);
                }),
                dynamicTest("주문을 완료한다.", () -> {
                    ExtractableResponse<Response> response = 주문_완료_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                })
        );
    }

    @DisplayName("매장 식사 주문 API 테스트")
    @TestFactory
    Stream<DynamicNode> eatInOrder() {
        return Stream.of(
                dynamicTest("데이터를 세팅한다.", () -> {
                    orderTable = 테이블_생성_요청("주문테이블1").as(OrderTable.class);
                    테이블_사용상태_변경_요청(orderTable);
                    product = 상품_생성_요청("치킨", BigDecimal.valueOf(15_000L)).as(Product.class);
                    menuGroup = 메뉴_그룹_생성_요청("menuGroup1").as(MenuGroup.class);
                    menu = 메뉴_생성_요청("치킨", BigDecimal.valueOf(15_000L), menuGroup.getId(), product).as(Menu.class);
                    orderLineItem = new OrderLineItem();
                    orderLineItem.setSeq(1L);
                    orderLineItem.setMenuId(menu.getId());
                    orderLineItem.setQuantity(1);
                    orderLineItem.setPrice(BigDecimal.valueOf(15_000L));
                }),
                dynamicTest("매장 식사 주문을 요청한다.", () -> {
                    ExtractableResponse<Response> response = 주문_요청(OrderType.EAT_IN, List.of(orderLineItem), null, orderTable);

                    주문_생성됨(response);
                    order = response.as(Order.class);
                }),
                dynamicTest("주문을 승인한다.", () -> {
                    ExtractableResponse<Response> response = 주문_승인_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                }),
                dynamicTest("주문을 고객에게 제공한다.", () -> {
                    ExtractableResponse<Response> response = 주문_제공_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                }),
                dynamicTest("매장 식사 주문은 배달 상태로 변경할 수 없다.", () -> {
                    ExtractableResponse<Response> response = 주문_배달_시작_요청(order.getId());

                    주문_상태_변경_실패됨(response);
                }),
                dynamicTest("주문을 완료한다.", () -> {
                    ExtractableResponse<Response> response = 주문_완료_요청(order.getId());

                    주문_상태_변경_응답됨(response);
                })
        );
    }

    public static ExtractableResponse<Response> 주문_요청(OrderType orderType,
                                                      List<OrderLineItem> orderLineItems,
                                                      String deliveryAddress,
                                                      OrderTable orderTable) {
        Map<String, Object> request = new HashMap<>();
        request.put("type", orderType);
        request.put("orderLineItems", orderLineItems);

        if (StringUtils.isNotBlank(deliveryAddress)) {
            request.put("deliveryAddress", deliveryAddress);
        }

        if (ObjectUtils.isNotEmpty(orderTable)) {
            request.put("orderTableId", orderTable.getId());
        }

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/orders")
                .then().log().all()
                .extract();
    }

    public static void 주문_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 주문_상태_변경_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_상태_변경_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 주문_승인_요청(UUID orderId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/{orderId}/accept", orderId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_제공_요청(UUID orderId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/{orderId}/serve", orderId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_배달_시작_요청(UUID orderId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/{orderId}/start-delivery", orderId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_배달_완료_요청(UUID orderId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/{orderId}/complete-delivery", orderId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_완료_요청(UUID orderId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/orders/{orderId}/complete", orderId)
                .then().log().all()
                .extract();
    }
}
