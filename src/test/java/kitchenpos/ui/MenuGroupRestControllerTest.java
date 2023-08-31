package kitchenpos.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("메뉴 그룹 API")
class MenuGroupRestControllerTest extends ControllerTest {

    @DisplayName("메뉴 그룹 API 테스트")
    @TestFactory
    Stream<DynamicNode> menuGroup() {
        return Stream.of(
                dynamicTest("메뉴 그룹을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_그룹_생성_요청("신메뉴");

                    메뉴_그룹_생성됨(response);
                }),
                dynamicTest("이름이 없는 메뉴 그룹을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_그룹_생성_요청(null);

                    메뉴_그룹_생성_실패됨(response);
                }),
                dynamicTest("메뉴 그룹 목록을 조회한다.", () -> {
                    ExtractableResponse<Response> response = 메뉴_그룹_목록_조회_요청();

                    메뉴_그룹_목록_응답됨(response);
                    메뉴_그룹_목록_확인됨(response, "신메뉴");
                })
        );
    }

    public static ExtractableResponse<Response> 메뉴_그룹_생성_요청(String name) {
        Map<String, Object> request = new HashMap<>();
        request.put("name", name);
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/menu-groups")
                .then().log().all()
                .extract();
    }


    public static ExtractableResponse<Response> 메뉴_그룹_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/menu-groups")
                .then().log().all()
                .extract();
    }

    public static void 메뉴_그룹_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 메뉴_그룹_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 메뉴_그룹_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 메뉴_그룹_목록_확인됨(ExtractableResponse<Response> response, String... names) {
        List<MenuGroup> menuGroups = response.jsonPath().getList(".", MenuGroup.class);

        List<String> productNames = menuGroups.stream()
                .map(MenuGroup::getName)
                .collect(Collectors.toList());
        assertThat(productNames).containsExactly(names);
    }
}
