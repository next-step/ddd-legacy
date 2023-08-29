package kitchenpos.acceptance.acceptance_step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

public class MenuGroupStep {
    private MenuGroupStep() {}

    public static ExtractableResponse<Response> 메뉴_그룹을_등록한다(MenuGroup menuGroup) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menuGroup)
                .when().post("/api/menu-groups")
                .then().log().all()
                .extract();
    }

    public static MenuGroup 메뉴_그룹_등록된_상태다() {
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(null)
                .changeName("메뉴그룹1")
                .getMenuGroup();
        return 메뉴_그룹을_등록한다(menuGroup).body().as(MenuGroup.class);
    }
}
