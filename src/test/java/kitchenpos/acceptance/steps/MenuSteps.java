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

    private static final String URI = "/api/menus/";

    public static ExtractableResponse<Response> 메뉴_생성(String name, BigDecimal price, UUID menuGroupId, List<MenuProduct> products) {
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

    public static ExtractableResponse<Response> 메뉴_가격_수정(UUID menuId, BigDecimal price) {
        Map<String, Object> params = new HashMap<>();
        params.put("price", price);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(URI+"{menuId}/price", menuId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_보이기(UUID menuId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI+"{menuId}/display", menuId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_숨기기(UUID menuId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI+"{menuId}/hide", menuId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_전체_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(URI)
                .then().log().all().extract();
    }
}
