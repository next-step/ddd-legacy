package kitchenpos.menugroup.acceptance;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import kitchenpos.support.AcceptanceTest;

import java.util.UUID;

import static kitchenpos.menugroup.acceptance.step.MenuGroupStep.메뉴_그룹_목록을_조회한다;
import static kitchenpos.menugroup.acceptance.step.MenuGroupStep.메뉴_그룹을_등록한다;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.A_메뉴그룹;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.B_메뉴그룹;
import static org.assertj.core.api.Assertions.assertThat;

public class MenuGroupAcceptanceTest extends AcceptanceTest {

    private static final String MENU_GROUP_ID_KEY = "id";

    /**
     * <pre>
     * when 메뉴 그룹을 등록하면
     * then 메뉴 그룹 목록 조회 시 등록한 메뉴 그룹을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {
        // when
        var 등록된_메뉴그룹 = 메뉴_그룹을_등록한다(A_메뉴그룹).as(MenuGroup.class);

        // then
        var 메뉴_그룹_아이디_목록 = 메뉴_그룹_목록을_조회한다()
                .jsonPath()
                .getList(MENU_GROUP_ID_KEY, UUID.class);
        assertThat(메뉴_그룹_아이디_목록).containsExactly(등록된_메뉴그룹.getId());
    }

    /**
     * <pre>
     * given 2개의 메뉴 그룹을 등록하고
     * when  메뉴 그룹 목록을 조회하면
     * then  등록한 2개의 메뉴 그룹을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {
        // given
        var 등록된_메뉴그룹_A = 메뉴_그룹을_등록한다(A_메뉴그룹).as(MenuGroup.class);
        var 등록된_메뉴그룹_B = 메뉴_그룹을_등록한다(B_메뉴그룹).as(MenuGroup.class);

        // when
        var 메뉴_그룹_아이디_목록 = 메뉴_그룹_목록을_조회한다()
                .jsonPath()
                .getList(MENU_GROUP_ID_KEY, UUID.class);

        // then
        assertThat(메뉴_그룹_아이디_목록).containsExactly(등록된_메뉴그룹_A.getId(), 등록된_메뉴그룹_B.getId());
    }

}
