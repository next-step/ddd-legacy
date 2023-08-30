package kitchenpos.acceptance.acceptance_step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class MenuGroupStep {
    private MenuGroupStep() {
    }

    public static ExtractableResponse<Response> 메뉴_그룹을_등록한다(MenuGroup menuGroup) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menuGroup)
                .when().post("/api/menu-groups")
                .then().log().all()
                .extract();
    }

    public static void 메뉴_그룹_등록됐다(ExtractableResponse<Response> response, String expectedName) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/api/menu-groups/" + response.body().as(MenuGroup.class).getId());
        MenuGroup 등록된_메뉴_그룹 = response.body().as(MenuGroup.class);
        assertThat(등록된_메뉴_그룹.getId()).isNotNull();
        assertThat(등록된_메뉴_그룹.getName()).isEqualTo(expectedName);
    }

    public static void 메뉴_그룹_등록에_실패한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static MenuGroup 메뉴_그룹_등록된_상태다() {
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(null)
                .changeName("메뉴그룹1")
                .getMenuGroup();
        return 메뉴_그룹을_등록한다(menuGroup).body().as(MenuGroup.class);
    }

    public static ExtractableResponse<Response> 등록된_전체_메뉴_그룹을_조회한다() {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/menu-groups")
                .then().log().all()
                .extract();
    }

    public static void 등록된_전체_메뉴_그룹을_조회에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<MenuGroup> menuGroups = response.body().jsonPath().getList(".", MenuGroup.class);
        menuGroups.forEach(it -> {
            assertThat(it.getId()).isNotNull();
            assertThat(it.getName()).isNotNull();
        });
    }
}
