package kitchenpos.ui.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import org.springframework.http.MediaType;

public class MenuGroupStep {
    private static final String PATH = "/api/menu-groups";
    public static ExtractableResponse<Response> 메뉴_그룹_생성_요청(MenuGroup param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(PATH)
                .then().log().all()
                .extract();
    }
}
