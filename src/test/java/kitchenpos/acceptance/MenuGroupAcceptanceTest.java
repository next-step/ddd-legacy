package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.acceptance.steps.MenuGroupSteps;
import kitchenpos.domain.Menu;
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
    void create() {
        //when
        ExtractableResponse<Response> response = MenuGroupSteps.메뉴그룹_생성(NAME);
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
    void findAll_test_1() {
        //given
        Menu menu1 = MenuGroupSteps.메뉴그룹_생성(NAME).as(Menu.class);
        Menu menu2 = MenuGroupSteps.메뉴그룹_생성(NAME).as(Menu.class);
        //when
        ExtractableResponse<Response> response = MenuGroupSteps.메뉴그룹_전체_조회();
        //then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .hasSize(2)
                        .contains(menu1.getId(), menu2.getId())
        );
    }
}
