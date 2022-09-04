package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuSteps {

    public static ExtractableResponse<Response> 메뉴_등록_요청(final RequestSpecification given, final Map<String, Object> params) {
        return given.body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/menus")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static UUID 메뉴가_등록됨(final RequestSpecification given,
                               final String name,
                               final int price,
                               final UUID menuGroupId,
                               final boolean displayed,
                               final List<Map> menuProducts) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price);
        params.put("menuGroupId", menuGroupId);
        params.put("displayed", displayed);
        params.put("menuProducts", menuProducts);

        return 메뉴_등록_요청(given, params).jsonPath().getUUID("id");
    }

    public static Map<String, Object> 메뉴상품을_구성함(final UUID productId, final int quantity) {
        Map<String, Object> menuProduct = new HashMap<>();
        menuProduct.put("productId", productId);
        menuProduct.put("quantity", quantity);
        return menuProduct;
    }

    public static ExtractableResponse<Response> 메뉴_목록_조회_요청(final RequestSpecification given) {
        return given
                .when().get("/api/menus")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴_가격_수정_요청(final RequestSpecification given, final UUID id, final Map<String, Object> params) {
        return given.body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/menus/{id}/price", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴_표시를_요청(final RequestSpecification given, final UUID id) {
        return given
                .when().put("/api/menus/{id}/display", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴_숨김을_요청(final RequestSpecification given, final UUID id) {
        return given
                .when().put("/api/menus/{id}/hide", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
