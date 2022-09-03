package kitchenpos.menugroup;

import kitchenpos.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static kitchenpos.menugroup.MenuGroupSteps.메뉴그룹_목록_조회_요청;
import static kitchenpos.menugroup.MenuGroupSteps.메뉴그룹_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메뉴 그룹")
class MenuGroupAcceptanceTest extends AcceptanceTest {
    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void create() {
        메뉴그룹_생성_요청("추천메뉴");

        assertThat(메뉴그룹_목록_조회_요청().jsonPath().getList("name")).containsExactly("추천메뉴");
    }

    @DisplayName("메뉴 그룹을 생성할 때 이름이 있어야 한다.")
    @Test
    void createWithNullName() {
        assertThat(메뉴그룹_생성_요청(null).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("메뉴그룹 목록을 조회한다.")
    @Test
    void findAll() {
        메뉴그룹_생성_요청("추천메뉴");
        메뉴그룹_생성_요청("이달의메뉴");
        메뉴그룹_생성_요청("계절메뉴");
        메뉴그룹_생성_요청("올해의메뉴");

        assertThat(메뉴그룹_목록_조회_요청().jsonPath().getList("name"))
                .containsExactly("추천메뉴", "이달의메뉴", "계절메뉴", "올해의메뉴");
    }
}
