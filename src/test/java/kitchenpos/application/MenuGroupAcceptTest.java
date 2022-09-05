package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.Stream;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("메뉴그룹 인수 테스트")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MenuGroupAcceptTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /*
      Feature: 메뉴 그룹을 관리.

        Scenario: 메뉴 그룹을 관리 한다.
            when 2개의 메뉴 그룹을 등록한다.
            then 2개 메뉴가 등록됨
            when 메뉴들을 조회 함
            then 등록된 메뉴가 조회 된다.
     */
    @DisplayName("메뉴 그룹 관리한다.")
    @TestFactory
    Stream<DynamicTest> menuGroupManage() {
        MenuGroup menuGroup1 = new MenuGroup();
        menuGroup1.setName("세트1");
        MenuGroup menuGroup2 = new MenuGroup();
        menuGroup2.setName("세트2");

        return Stream.of(
        dynamicTest("2개의 메뉴 그룹을 등록 한다.", () -> {
            final ExtractableResponse<Response> 세트1_메뉴그룹을_등록 = 메뉴그룹_등록을_요청(menuGroup1);
            final ExtractableResponse<Response> 세트2_메뉴그룹을_등록 = 메뉴그룹_등록을_요청(menuGroup2);

            assertAll(
                () -> assertThat(세트1_메뉴그룹을_등록.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(세트2_메뉴그룹을_등록.statusCode()).isEqualTo(HttpStatus.CREATED.value())
            );
        })
        ,dynamicTest("등록된 메뉴가 조회 된다.", () -> {

            final ExtractableResponse<Response> 등록된_메뉴그룹 = 메뉴그룹들을_조회();
            assertAll(
                    () -> assertThat(등록된_메뉴그룹.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(등록된_메뉴그룹.jsonPath().getList(".", MenuGroup.class))
                            .extracting("name")
                            .containsExactly(menuGroup1.getName(), menuGroup2.getName())
            );
        }));
    }



    @DisplayName("메뉴 그룹의 이름이 없으면 생성이 되지 않는다")
    @ParameterizedTest
    @NullAndEmptySource
    void noNameMenuGroup(String menuGroupName) {
        //given
        MenuGroup 이름없는_메뉴_그룹 = new MenuGroup();
        이름없는_메뉴_그룹.setName(menuGroupName);

        //when
        final ExtractableResponse<Response> createMenuGroup = 메뉴그룹_등록을_요청(이름없는_메뉴_그룹);

        //then
        assertThat(createMenuGroup.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 메뉴그룹_등록을_요청(MenuGroup menuGroup) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menuGroup)
                .when().post("/api/menu-groups")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 메뉴그룹들을_조회() {
        return RestAssured.given().log().all()
                .when().get("/api/menu-groups")
                .then()
                .log().all()
                .extract();
    }


}
