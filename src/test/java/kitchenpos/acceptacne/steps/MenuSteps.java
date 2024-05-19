package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.Menu;

import java.util.UUID;

public class MenuSteps {
    private static final String URI = "/api/menus";

    public static ExtractableResponse<Response> 메뉴_등록한다(Menu request) {
        return RestAssuredUtils.post(request, URI);
    }

    public static ExtractableResponse<Response> 메뉴의_가격을_수정한다(UUID id, Menu request) {
        return RestAssuredUtils.put(request, String.format(URI + "/%s/price", id));
    }

    public static ExtractableResponse<Response> 메뉴를_노출한다(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/display", id));
    }

    public static ExtractableResponse<Response> 메뉴를_숨긴다(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/hide", id));
    }

    public static ExtractableResponse<Response> 메뉴의_목록을_조회한다() {
        return RestAssuredUtils.get(URI);
    }
}
