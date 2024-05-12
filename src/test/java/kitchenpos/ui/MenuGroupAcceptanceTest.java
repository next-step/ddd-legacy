package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.AcceptanceTest;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static kitchenpos.acceptacne.steps.MenuGroupSteps.createMenuGroupStep;
import static kitchenpos.acceptacne.steps.MenuGroupSteps.getMenuGroupsStep;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuGroupFixture.이름_한마리메뉴;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@AcceptanceTest
@DisplayName("메뉴 그룹 인수 테스트")
class MenuGroupAcceptanceTest {

    @DisplayName("메뉴그룹 등록")
    @Nested
    class MenuGroupCreate {
        @DisplayName("[성공]")
        @Nested
        class Success {
            @DisplayName("메뉴그룹을 등록한다.")
            @Test
            void createMenuGroup() {
                // given
                MenuGroup request = menuGroupCreateRequest(이름_한마리메뉴);

                // when
                ExtractableResponse<Response> response = createMenuGroupStep(request);

                // then
                assertAll(
                        () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                        () -> assertThat(response.jsonPath().getString("id")).isNotEmpty(),
                        () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_한마리메뉴)
                );
            }
        }
    }

    @DisplayName("메뉴그룹 목록")
    @Nested
    class MenuGroupList {
        @DisplayName("[성공]")
        @Nested
        class Success {
            @DisplayName("메뉴그룹의 목록을 보여준다.")
            @Test
            void getMenuGroups() {
                // given
                UUID ID_한마리메뉴 = createMenuGroupId(이름_한마리메뉴);
                UUID ID_추천메뉴 = createMenuGroupId(이름_추천메뉴);

                // when
                ExtractableResponse<Response> response = getMenuGroupsStep();

                // then
                assertAll(
                        () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                        () -> assertThat(response.jsonPath().getList("id", UUID.class))
                                .containsExactly(ID_한마리메뉴, ID_추천메뉴),
                        () -> assertThat(response.jsonPath().getList("name"))
                                .containsExactly(이름_한마리메뉴, 이름_추천메뉴)
                );
            }
        }
    }

    private static UUID createMenuGroupId(String name) {
        return createMenuGroupStep(menuGroupCreateRequest(name)).as(MenuGroup.class).getId();
    }
}
