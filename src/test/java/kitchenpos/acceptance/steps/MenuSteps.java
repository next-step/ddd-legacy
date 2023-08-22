package kitchenpos.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuProduct;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuSteps {

    private static final String URI = "/api/menus";

    public static ExtractableResponse<Response> 메뉴를_생성한다(String name, BigDecimal price, UUID menuGroupId, List<MenuProduct> products) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price);
        params.put("menuGroupId", menuGroupId);
        params.put("menuProducts", products);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post(URI)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_가격을_수정한다(UUID menuId, BigDecimal price) {
        Map<String, Object> params = new HashMap<>();
        params.put("price", price);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(URI+"/{menuId}/price", menuId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴를_노출한다(UUID menuId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI+"/{menuId}/display", menuId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴를_숨긴다(UUID menuId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI+"/{menuId}/hide", menuId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_전체를_조회한다() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(URI)
                .then().log().all().extract();
    }
}
