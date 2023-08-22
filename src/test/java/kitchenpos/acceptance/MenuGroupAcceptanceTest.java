package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.acceptance.steps.MenuGroupSteps;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴그룹")
public class MenuGroupAcceptanceTest extends AcceptanceTest {

    private static final String NAME = "메뉴그룹";

    @DisplayName("[성공] 메뉴그룹 등록")
    @Test
    void createTest1() {
        //when
        ExtractableResponse<Response> response = MenuGroupSteps.메뉴그룹을_생성한다(NAME);
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value())
                , () -> assertThat(response.jsonPath().getString("name"))
                        .isEqualTo(NAME)
        );
    }

    @DisplayName("[성공] 메뉴그룹 전체조회")
    @Test
    void findAllTest1() {
        //given
        MenuGroup menuGroup1 = MenuGroupSteps.메뉴그룹을_생성한다(NAME).as(MenuGroup.class);
        MenuGroup menuGroup2 = MenuGroupSteps.메뉴그룹을_생성한다(NAME).as(MenuGroup.class);
        //when
        ExtractableResponse<Response> response = MenuGroupSteps.메뉴그룹_전체를_조회한다();
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .hasSize(2)
                        .contains(menuGroup1.getId(), menuGroup2.getId())
        );
    }
}
