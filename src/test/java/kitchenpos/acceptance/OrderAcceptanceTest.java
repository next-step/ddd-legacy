package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptance.fixture.MenuAcceptanceFixture;
import kitchenpos.acceptance.fixture.OrderTableAcceptanceFixture;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static kitchenpos.acceptance.fixture.AcceptanceSupport.*;
import static kitchenpos.domain.OrderType.*;
import static kitchenpos.domain.OrderType.EAT_IN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("주문 관련 기능")
public class OrderAcceptanceTest extends AcceptanceTest {
    private static final String ENDPOINT = "/api/orders";

    @DisplayName("매장 내 주문과 테이크 아웃 주문을 관리한다.")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"TAKEOUT", "EAT_IN"})
    void eatInManage(OrderType orderType) {
        // 주문을 생성 한다.
        ExtractableResponse<Response> response = createOrder(orderType);
        assertThat(response.statusCode()).isEqualTo(CREATED.value());

        // 주문을 조회 한다.
        ExtractableResponse<Response> findAllResponse = get(ENDPOINT);
        assertThat(findAllResponse.statusCode()).isEqualTo(OK.value());

        String orderId = (String) findAllResponse.body().jsonPath().getList("id").get(0);

        // 주문을 수락 한다
        ExtractableResponse<Response> acceptResponse = put(ENDPOINT + "/" + orderId + "/accept");
        assertThat(acceptResponse.statusCode()).isEqualTo(OK.value());

        // 주문을 제공 한다
        ExtractableResponse<Response> serveResponse = put(ENDPOINT + "/" + orderId + "/serve");
        assertThat(serveResponse.statusCode()).isEqualTo(OK.value());

        // 주문을 완료 한다.
        ExtractableResponse<Response> completeResponse = put(ENDPOINT + "/" + orderId + "/complete");
        assertThat(completeResponse.statusCode()).isEqualTo(OK.value());
    }

    @DisplayName("배달 주문을 관리한다.")
    @Test
    void DeliveryManage() {
        // 주문을 생성 한다
        ExtractableResponse<Response> response = createOrder(DELIVERY);
        assertThat(response.statusCode()).isEqualTo(CREATED.value());

        // 주문을 조회 한다.
        ExtractableResponse<Response> findAllResponse = get(ENDPOINT);
        assertThat(findAllResponse.statusCode()).isEqualTo(OK.value());

        String orderId = (String) findAllResponse.body().jsonPath().getList("id").get(0);

        // 주문을 수락 한다
        ExtractableResponse<Response> acceptResponse = put(ENDPOINT + "/" + orderId + "/accept");
        assertThat(acceptResponse.statusCode()).isEqualTo(OK.value());

        // 주문을 제공 한다
        ExtractableResponse<Response> serveResponse = put(ENDPOINT + "/" + orderId + "/serve");
        assertThat(serveResponse.statusCode()).isEqualTo(OK.value());

        // 배달을 시작 한다
        ExtractableResponse<Response> deliveryResponse = put(ENDPOINT + "/" + orderId + "/start-delivery");
        assertThat(deliveryResponse.statusCode()).isEqualTo(OK.value());

        // 배달을 완료 한다.
        ExtractableResponse<Response> completeDeliveryResponse = put(ENDPOINT + "/" + orderId + "/complete-delivery");
        assertThat(completeDeliveryResponse.statusCode()).isEqualTo(OK.value());

        // 주문을 완료 한다.
        ExtractableResponse<Response> completeResponse = put(ENDPOINT + "/" + orderId + "/complete");
        assertThat(completeResponse.statusCode()).isEqualTo(OK.value());
    }

    private ExtractableResponse<Response> createOrder(OrderType type) {
        // 주문에 필요한 메뉴를 생성 한다.
        ExtractableResponse<Response> menuResponse = createMenu();
        String menuId = (String) menuResponse.body().jsonPath().getList("id").get(0);
        float menuPrice = (float) menuResponse.body().jsonPath().getList("price").get(0);

        // 주문을 생성한다
        Map<String, Object> orderLineItem = new HashMap<>();
        orderLineItem.put("menuId", menuId);
        orderLineItem.put("price", menuPrice);
        orderLineItem.put("quantity", 3);

        Map<String, Object> createParams = new HashMap<>();
        createParams.put("type", type);
        createParams.put("orderLineItems", Arrays.asList(orderLineItem));

        // 주문에 필요한 주문 테이블을 생성 한다.
        if (type == EAT_IN) {
            String orderTableId = createOrderTable();
            createParams.put("orderTableId", orderTableId);
        }

        if (type == DELIVERY) {
            createParams.put("deliveryAddress", "강원도 양양군 현북면 남대천로 1418");
        }

        return post(createParams, ENDPOINT);
    }

    private String createOrderTable() {
        OrderTableAcceptanceFixture.createOrderTable();
        ExtractableResponse<Response> findAllResponse = OrderTableAcceptanceFixture.findAll();
        String orderTableId = (String) findAllResponse.body().jsonPath().getList("id").get(0);

        OrderTableAcceptanceFixture.sit(orderTableId);
        return orderTableId;
    }

    private ExtractableResponse<Response> createMenu() {
        MenuAcceptanceFixture.createMenu();
        return MenuAcceptanceFixture.findAllMenu();
    }
}
