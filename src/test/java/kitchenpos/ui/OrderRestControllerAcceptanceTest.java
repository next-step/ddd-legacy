package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static kitchenpos.ui.step.MenuGroupStep.메뉴_그룹_생성_요청;
import static kitchenpos.ui.step.MenuStep.메뉴_생성_요청;
import static kitchenpos.ui.step.OrderStep.*;
import static kitchenpos.ui.step.OrderTableStep.주문_테이블_배정_요청;
import static kitchenpos.ui.step.OrderTableStep.주문_테이블_생성_요청;
import static kitchenpos.ui.step.ProductStep.제품_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderRestControllerAcceptanceTest extends Acceptance {

    /**
     * 생성 -> 포장 -> 접수 -> 주문 완료
     *     -> 배달 -> 배달 요청 -> 배달 시작 -> 배달 완료 -> 주문 완료
     *     -> 매장 -> 서빙 -> 완료
     */
    @Test
    @DisplayName("포장 주문을 관리 한다.")
    void takeOutOrder() {
        // Arrange
        Menu menu = createMenu();
        UUID id = 메뉴_생성_요청(menu).jsonPath().getUUID("id");

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);
        orderLineItem.setMenuId(id);
        orderLineItem.setPrice(BigDecimal.TEN);

        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(Collections.singletonList(orderLineItem));

        // Act
        ExtractableResponse<Response> takeOutOrderCreateResponse = 주문_생성_요청(order);
        UUID takeOutOrderId = 주문_생성_요청(order).jsonPath().getUUID("id");

        // Assert
        포장_주문_확인(takeOutOrderCreateResponse);

        // Act
        ExtractableResponse<Response> acceptResponse = 주문_접수_요청(takeOutOrderId);

        // Assert
        주문_접수_확인(acceptResponse);

        // Act
        ExtractableResponse<Response> serveResponse = 주문_서빙_요청(takeOutOrderId);

        // Assert
        주문_서빙_확인(serveResponse);

        // Act
        ExtractableResponse<Response> completeResponse = 주문_완료_요청(takeOutOrderId);

        // Assert
        주문_완료_확인(completeResponse);
    }

    @DisplayName("배달 주문을 관리 한다.")
    @Test
    void deliveryOrder() {
        // Arrange
        Menu menu = createMenu();
        UUID id = 메뉴_생성_요청(menu).jsonPath().getUUID("id");

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);
        orderLineItem.setMenuId(id);
        orderLineItem.setPrice(BigDecimal.TEN);

        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(Collections.singletonList(orderLineItem));
        order.setDeliveryAddress("배달 주소");

        // Act
        ExtractableResponse<Response> deliveryOrderCreateResponse = 주문_생성_요청(order);
        UUID deliveryOrderId = 주문_생성_요청(order).jsonPath().getUUID("id");

        // Assert
        배달_주문_확인(deliveryOrderCreateResponse);

        // Act
        ExtractableResponse<Response> acceptResponse = 주문_접수_요청(deliveryOrderId);

        // Assert
        주문_접수_확인(acceptResponse);

        // Act
        ExtractableResponse<Response> serveResponse = 주문_서빙_요청(deliveryOrderId);

        // Assert
        주문_서빙_확인(serveResponse);

        // Act
        ExtractableResponse<Response> startDeliveryResponse = 주문_배달_시작_요청(deliveryOrderId);

        // Assert
        주문_배달_시작_확인(startDeliveryResponse);

        // Act
        ExtractableResponse<Response> completeDeliveryResponse = 주문_배달_완료_요청(deliveryOrderId);

        // Assert
        주문_배달_완료_확인(completeDeliveryResponse);

        // Act
        ExtractableResponse<Response> completeResponse = 주문_완료_요청(deliveryOrderId);

        // Assert
        주문_완료_확인(completeResponse);
    }

    @DisplayName("매장 주문을 관리 한다.")
    @Test
    void eatInOrder() {
        // Arrange
        Menu menu = createMenu();
        UUID id = 메뉴_생성_요청(menu).jsonPath().getUUID("id");

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);
        orderLineItem.setMenuId(id);
        orderLineItem.setPrice(BigDecimal.TEN);

        OrderTable orderTable = new OrderTable();
        orderTable.setName("orderTable name");
        orderTable.setNumberOfGuests(1);
        UUID orderTableId = 주문_테이블_생성_요청(orderTable).jsonPath().getUUID("id");
        주문_테이블_배정_요청(orderTableId);

        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(Collections.singletonList(orderLineItem));
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);

        // Act
        ExtractableResponse<Response> eatInOrderCreateResponse = 주문_생성_요청(order);
        UUID eatInOrderId = eatInOrderCreateResponse.jsonPath().getUUID("id");

        // Assert
        매장_주문_확인(eatInOrderCreateResponse);

        // Act
        ExtractableResponse<Response> acceptResponse = 주문_접수_요청(eatInOrderCreateResponse.jsonPath().getUUID("id"));

        // Assert
        주문_접수_확인(acceptResponse);

        // Act
        ExtractableResponse<Response> serveResponse = 주문_서빙_요청(eatInOrderId);

        // Assert
        주문_서빙_확인(serveResponse);

        // Act
        ExtractableResponse<Response> completeResponse = 주문_완료_요청(eatInOrderId);

        // Assert
        주문_완료_확인(completeResponse);
    }

    private void 주문_배달_완료_확인(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("status")).isEqualTo("DELIVERED");
    }

    private void 주문_배달_시작_확인(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("status")).isEqualTo("DELIVERING");
    }

    private void 주문_완료_확인(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("status")).isEqualTo("COMPLETED");
    }

    private void 주문_서빙_확인(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("status")).isEqualTo("SERVED");
    }

    private void 주문_접수_확인(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("status")).isEqualTo("ACCEPTED");
    }

    private void 매장_주문_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isEqualTo("/api/orders/" + response.jsonPath().getUUID("id")),
                () -> assertThat(response.jsonPath().getString("type")).isEqualTo("EAT_IN"),
                () -> assertThat(response.jsonPath().getString("status")).isEqualTo("WAITING")
        );
    }

    private void 배달_주문_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isEqualTo("/api/orders/" + response.jsonPath().getUUID("id")),
                () -> assertThat(response.jsonPath().getString("type")).isEqualTo("DELIVERY"),
                () -> assertThat(response.jsonPath().getString("status")).isEqualTo("WAITING")
        );
    }

    private void 포장_주문_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isEqualTo("/api/orders/" + response.jsonPath().getUUID("id")),
                () -> assertThat(response.jsonPath().getString("type")).isEqualTo("TAKEOUT"),
                () -> assertThat(response.jsonPath().getString("status")).isEqualTo("WAITING")
        );
    }

    private Menu createMenu() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹 이름");
        ExtractableResponse<Response> createMenuGroupResponse = 메뉴_그룹_생성_요청(menuGroup);
        UUID menuGroupId = createMenuGroupResponse.jsonPath().getUUID("id");

        Product product = new Product();
        product.setName("제품 이름");
        product.setPrice(BigDecimal.TEN);

        UUID productId = 제품_생성_요청(product).jsonPath().getUUID("id");

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(3);
        menuProduct.setProductId(productId);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        menu.setName("메뉴 이름");
        menu.setPrice(BigDecimal.TEN);
        menu.setDisplayed(true);

        return menu;
    }
}
