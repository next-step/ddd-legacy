package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.domain.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderAcceptanceStep {

    public static Response create(final Order order) {
        // @formatter:off
        final Response response = RestAssured
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

        final UUID orderId = response.getBody().jsonPath().getUUID("id");

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.getHeader("Location")).isEqualTo("/api/orders/" + orderId),
                () -> assertThat(orderId).isNotNull()
        );

        return response;
    }

    public static Response accept(final UUID orderId) {
        // @formatter:off
        final Response response = RestAssured
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

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }

    public static Response serve(final UUID orderId) {
        // @formatter:off
        final Response response = RestAssured
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

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }

    public static Response startDelivery(final UUID orderId) {
        // @formatter:off
        final Response response = RestAssured
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

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }

    public static Response completeDelivery(final UUID orderId) {
        // @formatter:off
        final Response response = RestAssured
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

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }

    public static Response complete(final UUID orderId) {
        // @formatter:off
        final Response response = RestAssured
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

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }

    public static Response findAll() {
        // @formatter:off
        final Response response =  RestAssured
                .given()
                    .log().all()
                .when()
                    .get("/api/orders")
                .then()
                    .log().all()
                    .extract()
                    .response();
        // @formatter:on

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }
}
