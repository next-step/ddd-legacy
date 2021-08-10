package kitchenpos.menu_group.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

public class MenuGroupStep {

    private static final String MENU_GROUP_URL = "/api/menu-groups";

    public static ExtractableResponse<Response> requestCreateMenuGroup(final MenuGroup menuGroup) {

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menuGroup)
                .when().post(MENU_GROUP_URL)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestFindAllMenuGroup() {
        return RestAssured
                .given().log().all()
                .when().get(MENU_GROUP_URL)
                .then().log().all().extract();
    }

    public static MenuGroup completeCreateMenuGroup(final MenuGroup menuGroup) {
        return requestCreateMenuGroup(menuGroup).as(MenuGroup.class);
    }

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        ReflectionTestUtils.setField(menuGroup, "name", name);
        return menuGroup;
    }
}
