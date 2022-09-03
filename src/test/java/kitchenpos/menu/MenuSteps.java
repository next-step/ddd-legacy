package kitchenpos.menu;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static kitchenpos.AcceptanceTestSteps.given;

public class MenuSteps {
    public static ExtractableResponse<Response> 메뉴_생성_요청(Menu menu) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().post("/api/menus")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴가격_변경_요청(String path, int price) {
        Map<String, String> params = new HashMap<>();
        params.put("price", price + "");

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(path + "/price")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_전시_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/display")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴_숨김_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/hide")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 메뉴목록_조회_요청() {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/menus")
                .then().log().all().extract();
    }
}
