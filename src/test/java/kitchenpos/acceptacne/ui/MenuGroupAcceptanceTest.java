package kitchenpos.acceptacne.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.AcceptanceTest;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static kitchenpos.acceptacne.steps.MenuGroupSteps.createMenuGroupStep;
import static kitchenpos.acceptacne.steps.MenuGroupSteps.getMenuGroupsStep;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroupRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@AcceptanceTest
@DisplayName("메뉴 그룹 인수 테스트")
class MenuGroupAcceptanceTest {
    private static final String 이름_한마리메뉴 = "한마리메뉴";
    private static final String 이름_추천메뉴 = "추천메뉴";

    @DisplayName("메뉴그룹을 등록한다.")
    @Test
    void createMenuGroup() {
        // given
        MenuGroup 한마리메뉴_요청 = createMenuGroupRequest(이름_한마리메뉴);

        // when
        ExtractableResponse<Response> response = createMenuGroupStep(한마리메뉴_요청);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getString("id")).isNotEmpty(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_한마리메뉴)
        );
    }

    @DisplayName("메뉴그룹의 목록을 보여준다.")
    @Test
    void getMenuGroups() {
        // given
        createMenuGroupStep(createMenuGroupRequest(이름_한마리메뉴));
        createMenuGroupStep(createMenuGroupRequest(이름_추천메뉴));

        // when
        ExtractableResponse<Response> response = getMenuGroupsStep();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).hasSize(2)
                .contains(이름_한마리메뉴, 이름_추천메뉴);
    }
}
