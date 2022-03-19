package kitchenpos.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuGroupRestControllerTest extends Acceptance {

    @Autowired
    ApplicationContext applicationContext;

    @DisplayName("메뉴 그룹 생성 테스트.")
    @Test
    void create() {
        // Arrange
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("name");

        // Act
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(menuGroup)
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/api/menu-groups")
                .then().log().all()
                .extract();

        UUID id = response.jsonPath().getUUID("id");

        // Assert
        assertAll(
                () -> assertThat(response.header("Location")).isEqualTo("/api/menu-groups/" + id),
                () -> assertThat(response.jsonPath().getString("id")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("name")
        );
    }

}