package kitchenpos.ui.requestor;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import org.springframework.http.MediaType;

import java.util.UUID;

public class MenuRequestor {

    private static final String DEFAULT_URL = "/api/menus";

    public static ExtractableResponse<Response> 메뉴생성요청(Menu menu) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

    public static UUID 메뉴생성요청_메뉴식별번호반환(Menu menu) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract().jsonPath().getObject("id", UUID.class);
    }

    public static ExtractableResponse<Response> 메뉴가격변경요청(UUID menuId, Menu menu) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("menuId", menuId)
                .body(menu)
                .when().put(DEFAULT_URL + "/{menuId}/price")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴노출요청(UUID menuId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("menuId", menuId)
                .when().put(DEFAULT_URL + "/{menuId}/display")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴비노출요청(UUID menuId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("menuId", menuId)
                .when().put(DEFAULT_URL + "/{menuId}/hide")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴전체조회요청() {
        return RestAssured.given().log().all()
                .when().get(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

}
