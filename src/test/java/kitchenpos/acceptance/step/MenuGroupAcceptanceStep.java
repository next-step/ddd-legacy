package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import org.springframework.http.MediaType;

public class MenuGroupAcceptanceStep {

    public static Response create(MenuGroup menuGroup) {
        // @formatter:off
        return RestAssured
                .given()
                    .log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(menuGroup)
                .when()
                    .post("/api/menu-groups")
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
                    .get("/api/menu-groups")
                .then()
                    .log().all()
                    .extract()
                    .response();
        // @formatter: on
    }
}
