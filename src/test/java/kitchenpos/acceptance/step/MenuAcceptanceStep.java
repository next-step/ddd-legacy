package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuAcceptanceStep {

    public static Response create(final Menu menu) {
        // @formatter:off
        final Response response =  RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(menu)
                .when()
                    .post("/api/menus")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        final UUID menuId = response.getBody().jsonPath().getUUID("id");

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.getHeader("Location")).isEqualTo("/api/menus/" + menuId),
                () -> assertThat(menuId).isNotNull()
        );

        return response;
    }

    public static Response changePrice(final UUID menuId, final Menu menu) {
        // @formatter:off
        final Response response =  RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("menuId", menuId)
                    .body(menu)
                .when()
                    .put("/api/menus/{menuId}/price")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.getBody().jsonPath().getObject("price", BigDecimal.class))
                        .isEqualTo(menu.getPrice())
        );

        return response;
    }

    public static Response display(final UUID menuId) {
        // @formatter:off
        final Response response = RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("menuId", menuId)
                .when()
                    .put("/api/menus/{menuId}/display")
                    .then()
                    .extract()
                .response();
        // @formatter:on

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.getBody().jsonPath().getBoolean("displayed")).isTrue()
        );

        return response;
    }

    public static Response hide(final UUID menuId) {
        // @formatter:off
        final Response response =  RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("menuId", menuId)
                .when()
                    .put("/api/menus/{menuId}/hide")
                .then()
                    .extract()
                    .response();
        // @formatter:on

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.getBody().jsonPath().getBoolean("displayed")).isFalse()
        );

        return response;
    }

    public static Response findAll() {
        // @formatter:off
        final Response response = RestAssured
                .given()
                    .log().all()
                .when()
                    .get("/api/menus")
                .then()
                    .log().all()
                    .extract()
                    .response();
        // @formatter: on

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );

        return response;
    }
}
