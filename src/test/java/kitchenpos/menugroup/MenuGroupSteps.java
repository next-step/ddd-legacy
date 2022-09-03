package kitchenpos.menugroup;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static kitchenpos.AcceptanceTestSteps.given;

public class MenuGroupSteps {
    public static ExtractableResponse<Response> 메뉴그룹_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/api/menu-groups")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴그룹_목록_조회_요청() {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/menu-groups")
                .then().log().all().extract();
    }
}
