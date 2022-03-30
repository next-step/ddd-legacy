package kitchenpos.ui.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import org.springframework.http.MediaType;

import java.util.UUID;

public class MenuStep {

    private static final String PATH = "/api/menus";

    public static ExtractableResponse<Response> 메뉴_생성_요청(Menu param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴_가격_변경_요청(UUID id, Menu param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{menuId}/price", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴_전시하기_요청(UUID id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{menuId}/display", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴_숨기기_요청(UUID id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{menuId}/hide", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 모든_메뉴_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(PATH)
                .then().log().all()
                .extract();
    }

}
