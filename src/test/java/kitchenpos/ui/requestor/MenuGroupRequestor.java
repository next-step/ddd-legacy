package kitchenpos.ui.requestor;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import org.springframework.http.MediaType;

public class MenuGroupRequestor {

    private static final String DEFAULT_URL = "/api/menu-groups";

    public static ExtractableResponse<Response> 메뉴그룹생성요청(MenuGroup menuGroup) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menuGroup)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴그룹전체조회요청() {
        return RestAssured.given().log().all()
                .when().get(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

}
