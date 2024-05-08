package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.Menu;

import java.util.UUID;

public class MenuSteps {
    private static final String URI = "/api/menus";

    public static ExtractableResponse<Response> createMenuStep(Menu request) {
        return RestAssuredUtils.post(request, URI);
    }

    public static ExtractableResponse<Response> changePriceMenuStep(UUID id, Menu request) {
        return RestAssuredUtils.put(request, String.format(URI + "/%s/price", id));
    }

    public static ExtractableResponse<Response> displayMenuStep(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/display", id));
    }

    public static ExtractableResponse<Response> hideMenuStep(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/hide", id));
    }

    public static ExtractableResponse<Response> getMenusStep() {
        return RestAssuredUtils.get(URI);
    }
}
