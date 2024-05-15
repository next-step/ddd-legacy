package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import kitchenpos.acceptance.step.MenuGroupAcceptanceStep;
import kitchenpos.config.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;

@AcceptanceTest
class MenuGroupAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("메뉴 그룹을 생성 하고 조회할 수 있다.")
    @Test
    void menuGroupCreateAndFindAllAcceptance() {
        Response 추천메뉴 = MenuGroupAcceptanceStep.create(createMenuGroup(null, "추천메뉴"));

        JsonPath createBody = 추천메뉴.getBody().jsonPath();

        assertThat(추천메뉴.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(추천메뉴.header("Location")).isEqualTo("/api/menu-groups/" + createBody.getString("id"));
        assertThat(createBody.getString("id")).isNotNull();
        assertThat(createBody.getString("name")).isEqualTo("추천메뉴");

        Response 기본메뉴 = MenuGroupAcceptanceStep.create(createMenuGroup(null, "기본메뉴"));

        Response findAll = MenuGroupAcceptanceStep.findAll();
        JsonPath createBody2 = 기본메뉴.getBody().jsonPath();

        JsonPath findAllBody = findAll.getBody().jsonPath();
        assertThat(findAll.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findAllBody.getList("name")).contains("추천메뉴", "기본메뉴");
        assertThat(findAllBody.getList("id")).contains(createBody.getString("id"), createBody2.getString("id"));
    }
}
