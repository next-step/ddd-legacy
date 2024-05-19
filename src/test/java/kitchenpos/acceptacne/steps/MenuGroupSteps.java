package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.MenuGroup;

public class MenuGroupSteps {
    private static final String URI = "/api/menu-groups";

    public static ExtractableResponse<Response> 메뉴그룹을_등록한다(MenuGroup params) {
        return RestAssuredUtils.post(params, URI);
    }

    public static ExtractableResponse<Response> 메뉴그룹_목록을_보여준다() {
        return RestAssuredUtils.get(URI);
    }
}
