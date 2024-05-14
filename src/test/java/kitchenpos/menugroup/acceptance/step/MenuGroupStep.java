package kitchenpos.menugroup.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import kitchenpos.support.RestAssuredClient;

public class MenuGroupStep {

    private static final String MENU_GROUP_BASE_URL = "/api/menu-groups";

    public static ExtractableResponse<Response> 메뉴_그룹을_생성한다(MenuGroup request) {
        return RestAssuredClient.post(MENU_GROUP_BASE_URL, request);
    }

    public static ExtractableResponse<Response> 메뉴_그룹_목록을_조회한다() {
        return RestAssuredClient.get(MENU_GROUP_BASE_URL);
    }

}
