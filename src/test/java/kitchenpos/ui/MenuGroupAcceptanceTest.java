package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.AcceptanceTest;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static kitchenpos.acceptacne.steps.MenuGroupSteps.createMenuGroupStep;
import static kitchenpos.acceptacne.steps.MenuGroupSteps.getMenuGroupsStep;
import static kitchenpos.fixture.MenuGroupFixture.NAME_추천메뉴;
import static kitchenpos.fixture.MenuGroupFixture.NAME_한마리메뉴;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@AcceptanceTest
@DisplayName("메뉴 그룹 인수 테스트")
class MenuGroupAcceptanceTest {
    private MenuGroup MENU_GROUP_한마리메뉴;
    private MenuGroup MENU_GROUP_추천메뉴;

    @DisplayName("메뉴그룹을 등록한다.")
    @Test
    void createMenuGroup() {
        // given
        MENU_GROUP_한마리메뉴 = menuGroupCreateRequest(NAME_한마리메뉴);

        // when
        ExtractableResponse<Response> response = createMenuGroupStep(MENU_GROUP_한마리메뉴);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getString("id")).isNotEmpty(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME_한마리메뉴)
        );
    }

    @DisplayName("메뉴그룹의 목록을 보여준다.")
    @Test
    void getMenuGroups() {
        // given
        MENU_GROUP_한마리메뉴 = createMenuGroupStep(menuGroupCreateRequest(NAME_한마리메뉴)).as(MenuGroup.class);
        MENU_GROUP_추천메뉴 = createMenuGroupStep(menuGroupCreateRequest(NAME_추천메뉴)).as(MenuGroup.class);

        // when
        ExtractableResponse<Response> response = getMenuGroupsStep();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id", UUID.class)).hasSize(2)
                        .contains(MENU_GROUP_한마리메뉴.getId(), MENU_GROUP_추천메뉴.getId()),
                () -> assertThat(response.jsonPath().getList("name")).hasSize(2)
                        .contains(MENU_GROUP_한마리메뉴.getName(), MENU_GROUP_추천메뉴.getName())
        );
    }
}
