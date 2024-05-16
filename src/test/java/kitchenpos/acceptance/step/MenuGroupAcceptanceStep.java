package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuGroupAcceptanceStep {

    public static Response create(final MenuGroup menuGroup) {
        // @formatter:off
        final Response response =  RestAssured
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

        final UUID menuGroupId = response.getBody().jsonPath().getUUID("id");

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.getHeader("Location")).isEqualTo("/api/menu-groups/" + menuGroupId),
                () -> assertThat(response.getBody().jsonPath().getUUID("id")).isNotNull()
        );

        return response;
    }

    public static Response findAll() {
        // @formatter:off
        final Response response = RestAssured
                .given()
                    .log().all()
                .when()
                    .get("/api/menu-groups")
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
