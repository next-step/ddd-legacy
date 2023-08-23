package kitchenpos.acceptance;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("메뉴 그룹 관리")
class MenuGroupAcceptanceTest extends AcceptanceTest {

    private static final String path = "/api/menu-groups";

    /**
     * When : 메뉴그룹을 3개 생성하고
     * Then : 전체 메뉴그룹을 조회하면 등록한 메뉴그룹 찾을 수 있다.
     */
    @DisplayName("메뉴가 그룹 생성")
    @Test
    void create() {
        //when
        메뉴그룹을_등록_한다("한식");
        메뉴그룹을_등록_한다("양식");
        메뉴그룹을_등록_한다("분식");

        //then
        ExtractableResponse<Response> response = 메뉴그룹을_조회_한다();
        Assertions.assertThat(response.jsonPath().getList("name"))
            .containsOnly("한식", "양식", "분식");
    }

    public ExtractableResponse<Response> 메뉴그룹을_등록_한다(String name) {
        return RestAssured.given().log().all()
            .body(Map.of("name", name))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    public ExtractableResponse<Response> 메뉴그룹을_조회_한다() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }
}
