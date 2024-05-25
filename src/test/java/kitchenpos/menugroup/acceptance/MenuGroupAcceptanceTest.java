package kitchenpos.menugroup.acceptance;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import kitchenpos.support.AcceptanceTest;

import java.util.UUID;

import static kitchenpos.menugroup.acceptance.step.MenuGroupStep.메뉴_그룹_목록을_조회한다;
import static kitchenpos.menugroup.acceptance.step.MenuGroupStep.메뉴_그룹을_등록한다;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.양식;
import static kitchenpos.menugroup.fixture.MenuGroupFixture.한식;
import static org.assertj.core.api.Assertions.assertThat;

public class MenuGroupAcceptanceTest extends AcceptanceTest {

    private static final String MENU_GROUP_ID_KEY = "id";

    /**
     * <pre>
     * when 한식 그룹을 등록한다.
     * then 메뉴 그룹 목록 조회 시 한식 그룹을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("등록")
    void create() {
        // when
        var 등록된_메뉴_그룹_한식 = 메뉴_그룹을_등록한다(한식).as(MenuGroup.class);

        // then
        var 메뉴_그룹_아이디_목록 = 메뉴_그룹_목록을_조회한다()
                .jsonPath()
                .getList(MENU_GROUP_ID_KEY, UUID.class);
        assertThat(메뉴_그룹_아이디_목록).containsExactly(등록된_메뉴_그룹_한식.getId());
    }

    /**
     * <pre>
     * given 한식 그룹과 양식 그룹을 등록한다.
     * when  메뉴 그룹 목록을 조회한다.
     * then  한식 그룹과 양식 그룹을 찾을 수 있다.
     * </pre>
     */
    @Test
    @DisplayName("목록조회")
    void findAll() {
        // given
        var 등록된_메뉴_그룹_한식 = 메뉴_그룹을_등록한다(한식).as(MenuGroup.class);
        var 등록된_메뉴_그룹_양식 = 메뉴_그룹을_등록한다(양식).as(MenuGroup.class);

        // when
        var 메뉴_그룹_아이디_목록 = 메뉴_그룹_목록을_조회한다()
                .jsonPath()
                .getList(MENU_GROUP_ID_KEY, UUID.class);

        // then
        assertThat(메뉴_그룹_아이디_목록).containsExactly(등록된_메뉴_그룹_한식.getId(), 등록된_메뉴_그룹_양식.getId());
    }

}
