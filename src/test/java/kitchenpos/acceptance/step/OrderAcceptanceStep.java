package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.domain.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderAcceptanceStep {

    public static Response create(Order order) {
        // @formatter:off
        Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(order)
                .when()
                    .post("/api/orders")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return response;
    }

    public static Response accept(UUID orderId) {
        // @formatter:off
        Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("orderId", orderId)
                .when()
                    .put("/api/orders/{orderId}/accept")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response;
    }

    public static Response serve(UUID orderId) {
        // @formatter:off
        Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("orderId", orderId)
                .when()
                    .put("/api/orders/{orderId}/serve")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response;
    }

    public static Response startDelivery(UUID orderId) {
        // @formatter:off
        Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("orderId", orderId)
                .when()
                    .put("/api/orders/{orderId}/start-delivery")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response;
    }

    public static Response completeDelivery(UUID orderId) {
        // @formatter:off
        Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("orderId", orderId)
                .when()
                    .put("/api/orders/{orderId}/complete-delivery")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response;
    }

    public static Response complete(UUID orderId) {
        // @formatter:off
        Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("orderId", orderId)
                .when()
                    .put("/api/orders/{orderId}/complete")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response;
    }

    public static Response findAll() {
        // @formatter:off
        Response response =  RestAssured
                .given()
                    .log().all()
                .when()
                    .get("/api/orders")
                .then()
                    .log().all()
                    .extract()
                    .response();
        // @formatter:on

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        
        return response;
    }
}
