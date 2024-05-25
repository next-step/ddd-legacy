package kitchenpos.menu.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.support.RestAssuredClient;

import java.util.UUID;

public class MenuStep {

    private static final String MENU_BASE_URL = "/api/menus";

    public static ExtractableResponse<Response> 메뉴를_등록한다(Menu request) {
        return RestAssuredClient.post(MENU_BASE_URL, request);
    }

    public static ExtractableResponse<Response> 메뉴_목록을_조회한다() {
        return RestAssuredClient.get(MENU_BASE_URL);
    }

    public static ExtractableResponse<Response> 메뉴_가격을_수정한다(Menu request) {
        var url = String.format("%s/%s/price", MENU_BASE_URL, request.getId());
        return RestAssuredClient.put(url, request);
    }

    public static ExtractableResponse<Response> 메뉴를_숨김해제처리_한다(UUID request) {
        var url = String.format("%s/%s/display", MENU_BASE_URL, request);
        return RestAssuredClient.put(url, request);
    }

    public static ExtractableResponse<Response> 메뉴를_숨김처리_한다(UUID request) {
        var url = String.format("%s/%s/hide", MENU_BASE_URL, request);
        return RestAssuredClient.put(url, request);
    }
}
