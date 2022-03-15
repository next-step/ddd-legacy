package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderSteps {
    private static final String ENDPOINT = "/api/orders";

    public static ExtractableResponse<Response> 배달_주문_생성_요청(Map<String, String>... menus) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createDeliveryParams(menus))
                .when().post(ENDPOINT)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 포장_주문_생성_요청(Map<String, String>... menus) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createTakeoutParams(menus))
                .when().post(ENDPOINT)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 매장_식사_주문_생성_요청(Map<String, String> orderTable, Map<String, String>... menus) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createEatInParams(orderTable, menus))
                .when().post(ENDPOINT)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_수락_상태_변경_요청(ExtractableResponse<Response> createResponse) {
        String id = createResponse.body().jsonPath().getString("id");
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT + "/{orderId}/accept", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_제공_상태_변경_요청(ExtractableResponse<Response> acceptResponse) {
        String id = acceptResponse.body().jsonPath().getString("id");
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT + "/{orderId}/serve", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_배달중_상태_변경_요청(ExtractableResponse<Response> serveResponse) {
        String id = serveResponse.body().jsonPath().getString("id");
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT + "/{orderId}/start-delivery", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_배달완료_상태_변경_요청(ExtractableResponse<Response> startDeliveryResponse) {
        String id = startDeliveryResponse.body().jsonPath().getString("id");
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT + "/{orderId}/complete-delivery", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_완료_상태_변경_요청(ExtractableResponse<Response> response) {
        String id = response.body().jsonPath().getString("id");
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT + "/{orderId}/complete", id)
                .then().log().all().extract();
    }

    public static void 주문_생성_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 주문_수락_상태_변경_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_제공_상태_변경_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_배달중_상태_변경_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_배달완료_상태_변경_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_완료_상태_변경_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static Map<String, Object> createDeliveryParams(Map<String, String>[] menus) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", OrderType.DELIVERY);
        params.put("status", OrderStatus.WAITING);
        params.put("orderDateTime", LocalDateTime.now());
        params.put("deliveryAddress", "서울시 마포구");
        params.put("orderLineItems", createOrderLineItems(menus));
        return params;
    }

    private static Map<String, Object> createTakeoutParams(Map<String, String>[] menus) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", OrderType.TAKEOUT);
        params.put("status", OrderStatus.WAITING);
        params.put("orderDateTime", LocalDateTime.now());
        params.put("orderLineItems", createOrderLineItems(menus));
        return params;
    }

    private static Map<String, Object> createEatInParams(Map<String, String> orderTable, Map<String, String>[] menus) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", OrderType.EAT_IN);
        params.put("status", OrderStatus.WAITING);
        params.put("orderDateTime", LocalDateTime.now());
        params.put("orderLineItems", createOrderLineItems(menus));
        params.put("orderTable", orderTable);
        params.put("orderTableId", orderTable.get("id"));
        return params;
    }

    private static List<Map<String, Object>> createOrderLineItems(Map<String, String>[] menus) {
        List<Map<String, Object>> orderLineItems = new ArrayList<>();

        Arrays.stream(menus).forEach(m -> {
            Map<String, Object> orderLineItem = new HashMap<>();
            orderLineItem.put("menu", m);
            orderLineItem.put("quantity", "1");
            orderLineItem.put("price", m.get("price"));
            orderLineItem.put("menuId", m.get("id"));
            orderLineItems.add(orderLineItem);
        });
        return orderLineItems;
    }
}
