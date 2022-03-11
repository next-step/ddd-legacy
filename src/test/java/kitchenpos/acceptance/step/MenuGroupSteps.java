package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MenuGroupSteps {
    private static final String ENDPOINT = "/api/menu-groups";

    public static ExtractableResponse<Response> 메뉴_그룹_등록_요청(String name) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createParams(name))
                .when().post(ENDPOINT)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_그룹_목록_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(ENDPOINT)
                .then().log().all().extract();
    }

    public static void 메뉴_그룹_생성_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 메뉴_그룹_목록_조회_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static Map<String, String> createParams(String name) {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        return params;
    }
}
