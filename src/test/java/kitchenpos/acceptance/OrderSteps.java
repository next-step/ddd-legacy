package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.UUID;
import kitchenpos.domain.Order;
import org.springframework.http.MediaType;

public class OrderSteps {

  public static ExtractableResponse<Response> createOrder(Order order) {
    return RestAssured
        .given().log().all()
        .body(order)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().post("/api/orders")
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> orderAccept(UUID orderId) {
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/orders/{orderId}/accept", orderId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> orderServe(UUID orderId) {
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/orders/{orderId}/serve", orderId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> orderDeliveryStart(UUID orderId) {
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/orders/{orderId}/start-delivery", orderId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> orderDeliveryComplete(UUID orderId) {
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/orders/{orderId}/complete-delivery", orderId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> ordercomplete(UUID orderId) {
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().put("/api/orders/{orderId}/complete", orderId)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> getOrder() {
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().get("/api/orders")
        .then().log().all().extract();
  }
}
