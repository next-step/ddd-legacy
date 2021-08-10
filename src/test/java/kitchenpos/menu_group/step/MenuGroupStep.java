package kitchenpos.menu_group.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

public class MenuGroupStep {

    public static ExtractableResponse<Response> requestCreateMenuGroup(final MenuGroup menuGroup) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menuGroup)
                .when().post("/api/menu-groups")
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
