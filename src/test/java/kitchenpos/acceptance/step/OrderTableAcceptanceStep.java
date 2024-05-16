package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderTableAcceptanceStep {

    public static Response create(final OrderTable orderTable) {
        // @formatter:off
        final Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(orderTable)
                .when()
                    .post("/api/order-tables")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        final UUID orderTableId = response.getBody().jsonPath().getUUID("id");

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isEqualTo("/api/order-tables/" + orderTableId),
                () -> assertThat(orderTableId).isNotNull()
        );

        return response;
    }

    public static Response sit(final UUID orderTableId) {
        // @formatter:off
        final Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("orderTableId", orderTableId)
                .when()
                    .put("/api/order-tables/{orderTableId}/sit")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }

    public static Response clear(final UUID orderTableId) {
        // @formatter:off
        final Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("orderTableId", orderTableId)
                .when()
                    .put("/api/order-tables/{orderTableId}/clear")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }

    public static Response changeNumberOfGuests(final UUID orderTableId, final OrderTable orderTable) {
        // @formatter:off
        final Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("orderTableId", orderTableId)
                    .body(orderTable)
                .when()
                    .put("/api/order-tables/{orderTableId}/number-of-guests")
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
        final Response response = RestAssured
                .given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/order-tables")
                .then()
                .extract()
                .response();
        // @formatter:on

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }
}
