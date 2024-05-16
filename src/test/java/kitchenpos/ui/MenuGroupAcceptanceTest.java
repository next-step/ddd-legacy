package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.AcceptanceTest;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static kitchenpos.acceptacne.steps.MenuGroupSteps.메뉴그룹_목록을_보여준다;
import static kitchenpos.acceptacne.steps.MenuGroupSteps.메뉴그룹을_등록한다;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuGroupFixture.이름_한마리메뉴;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@AcceptanceTest
@DisplayName("메뉴 그룹 인수 테스트")
class MenuGroupAcceptanceTest {
    @DisplayName("[성공] 메뉴그룹을 등록한다.")
    @Test
    void createMenuGroup() {
        // given
        MenuGroup 한마리메뉴_등록_요청 = 메뉴그룹_등록_요청(이름_한마리메뉴);

        // then
        var 한마리메뉴_등록_응답 = 메뉴그룹을_등록한다(한마리메뉴_등록_요청);

        // then
        메뉴그룹_등록_검증(한마리메뉴_등록_응답);
    }

    @DisplayName("[성공] 메뉴그룹의 목록을 보여준다.")
    @Test
    void getMenuGroups() {
        // given
        UUID ID_한마리메뉴 = 메뉴그룹을_등록하고_해당_ID를_반환한다(이름_한마리메뉴);
        UUID ID_추천메뉴 = 메뉴그룹을_등록하고_해당_ID를_반환한다(이름_추천메뉴);

        // when
        var 메뉴그룹_목록_응답 = 메뉴그룹_목록을_보여준다();

        // then
        메뉴그룹_목록_검증(ID_한마리메뉴, ID_추천메뉴, 메뉴그룹_목록_응답);
    }

    private static void 메뉴그룹_목록_검증(UUID ID_한마리메뉴, UUID ID_추천메뉴, ExtractableResponse<Response> 메뉴그룹_목록_응답) {
        assertAll(
                () -> assertThat(메뉴그룹_목록_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(메뉴그룹_목록_응답.jsonPath().getList("id", UUID.class))
                        .containsExactly(ID_한마리메뉴, ID_추천메뉴),
                () -> assertThat(메뉴그룹_목록_응답.jsonPath().getList("name"))
                        .containsExactly(이름_한마리메뉴, 이름_추천메뉴)
        );
    }

    private static void 메뉴그룹_등록_검증(ExtractableResponse<Response> 한마리메뉴_등록_응답) {
        assertAll(
                () -> assertThat(한마리메뉴_등록_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(한마리메뉴_등록_응답.jsonPath().getString("id")).isNotEmpty(),
                () -> assertThat(한마리메뉴_등록_응답.jsonPath().getString("name")).isEqualTo(이름_한마리메뉴)
        );
    }

    private static UUID 메뉴그룹을_등록하고_해당_ID를_반환한다(String name) {
        return 메뉴그룹을_등록한다(메뉴그룹_등록_요청(name)).as(MenuGroup.class).getId();
    }

    private static MenuGroup 메뉴그룹_등록_요청(String name) {
        return menuGroupCreateRequest(name);
    }
}
