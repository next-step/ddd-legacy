package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;

import java.util.Map;

public class MenuGroupSteps {
    private static final String URI = "/api/menu-groups";

    public static ExtractableResponse<Response> createMenuGroupStep(Map<String, Object> params) {
        return RestAssuredUtils.post(params, URI);
    }

    public static ExtractableResponse<Response> getMenuGroupsStep() {
        return RestAssuredUtils.get(URI);
    }
}
