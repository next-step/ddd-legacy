package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static kitchenpos.acceptance.acceptance_step.MenuGroupStep.*;

@DisplayName("메뉴 그룹 인수 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MenuGroupAcceptanceTest extends AcceptanceTestBase {

    @Test
    void 메뉴_그룹_등록에_성공한다() {
        // given
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(null)
                .changeName("메뉴그룹1")
                .getMenuGroup();

        // when
        ExtractableResponse<Response> response = 메뉴_그룹을_등록한다(menuGroup);

        // then
        메뉴_그룹_등록됐다(response, "메뉴그룹1");
    }

    @Test
    void 메뉴_그룹_등록_시_이름을_설정하지_않으면_등록에_실패한다() {
        // given
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(null)
                .changeName(null)
                .getMenuGroup();

        // when
        ExtractableResponse<Response> response = 메뉴_그룹을_등록한다(menuGroup);

        // then
        메뉴_그룹_등록에_실패한다(response);
    }

    @Test
    void 메뉴_그룹_등록_시_이름이_빈_문자열이면_등록에_실패한다() {
        // given
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(null)
                .changeName("")
                .getMenuGroup();

        // when
        ExtractableResponse<Response> response = 메뉴_그룹을_등록한다(menuGroup);

        // then
        메뉴_그룹_등록에_실패한다(response);
    }

    @Test
    void 등록된_전체_메뉴_그룹을_조회에_성공한다() {
        // given
        메뉴_그룹_등록된_상태다();
        메뉴_그룹_등록된_상태다();
        메뉴_그룹_등록된_상태다();

        // when
        ExtractableResponse<Response> response = 등록된_전체_메뉴_그룹을_조회한다();

        // then
        등록된_전체_메뉴_그룹을_조회에_성공했다(response);
    }
}
