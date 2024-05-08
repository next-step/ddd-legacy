package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.MenuGroup;

public class MenuGroupSteps {
    private static final String URI = "/api/menu-groups";

    public static ExtractableResponse<Response> createMenuGroupStep(MenuGroup params) {
        return RestAssuredUtils.post(params, URI);
    }

    public static ExtractableResponse<Response> getMenuGroupsStep() {
        return RestAssuredUtils.get(URI);
    }
}
