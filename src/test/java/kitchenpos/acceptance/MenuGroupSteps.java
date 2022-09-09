package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kitchenpos.domain.MenuGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

public class MenuGroupSteps {

    public static ExtractableResponse<Response> 메뉴그룹_등록_요청(final RequestSpecification given, final MenuGroup menuGroup) {
        return given.body(menuGroup)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/menu-groups")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static UUID 메뉴그룹이_등록됨(final RequestSpecification given, final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        return 메뉴그룹_등록_요청(given, menuGroup).jsonPath().getUUID("id");
    }

    public static ExtractableResponse<Response> 메뉴그룹_목록_조회_요청(final RequestSpecification given) {
        return given
                .when().get("/api/menu-groups")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
