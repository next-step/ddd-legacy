package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import java.util.Map;

public class menuGroupSteps {

    public static ExtractableResponse<Response> 메뉴그룹_등록_요청(final RequestSpecification given, final Map<String, String> params) {
        return given.body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/menu-groups")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴그룹_목록_조회_요청(final RequestSpecification given) {
        return given
                .when().get("/api/menu-groups")
                .then().log().all().extract();
    }
}
