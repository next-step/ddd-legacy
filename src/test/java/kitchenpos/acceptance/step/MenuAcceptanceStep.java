package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import org.springframework.http.MediaType;

import java.util.UUID;

public class MenuAcceptanceStep {

    public static Response create(Menu menu) {
        // @formatter:off
        return RestAssured
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
    }

    public static Response changePrice(UUID menuId, Menu menu) {
        // @formatter:off
        return RestAssured
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
    }

    public static Response display(UUID menuId, Menu menu) {
        // @formatter:off
        return RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("menuId", menuId)
                    .body(menu)
                .when()
                    .put("/api/menus/{menuId}/display")
                    .then()
                    .extract()
                .response();
        // @formatter:on
    }

    public static Response hide(UUID menuId, Menu menu) {
        // @formatter:off
        return RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("menuId", menuId)
                    .body(menu)
                .when()
                    .put("/api/menus/{menuId}/hide")
                .then()
                    .extract()
                    .response();
        // @formatter:on
    }

    public static Response findAll() {
        // @formatter:off
        return RestAssured
                .given()
                    .log().all()
                .when()
                    .get("/api/menus")
                .then()
                    .log().all()
                    .extract()
                    .response();
        // @formatter: on
    }
}
