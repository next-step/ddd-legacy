package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixture.DEFAULT_MENU_GROUP_NAME;
import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Test
    void 유효한_이름으로_메뉴_그룹을_생성하면_메뉴_그룹이_정상적으로_생성된다() {
        // given
        final MenuGroup request = menuGroup(null, DEFAULT_MENU_GROUP_NAME);

        // when
        final MenuGroup response = menuGroupService.create(request);

        // then
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    void 메뉴_그룹_생성_시_이름이_NULL이면_예외가_발생한다() {
        // given
        final MenuGroup request = menuGroup(null, null);

        // when & then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuGroupService.create(request));
    }

    @Test
    void 메뉴_그룹_생성_시_이름이_빈_문자열이면_예외가_발생한다() {
        // given
        final MenuGroup request = menuGroup(null, "");

        // when & then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuGroupService.create(request));
    }

    @Test
    void 전체_메뉴_그룹_조회_시_생성된_모든_메뉴_그룹이_반환된다() {
        // given
        final MenuGroup request1 = menuGroup(null, DEFAULT_MENU_GROUP_NAME);
        menuGroupService.create(request1);

        final MenuGroup request2 = menuGroup(null, "피자 메뉴");
        menuGroupService.create(request2);

        // when
        List<MenuGroup> response = menuGroupService.findAll();

        // then
        assertThat(response).hasSize(2);
    }

}
