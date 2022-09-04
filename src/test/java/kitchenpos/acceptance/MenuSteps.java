package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
