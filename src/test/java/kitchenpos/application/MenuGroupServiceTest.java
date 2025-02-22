package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;


class MenuGroupServiceTest {

    private MenuGroupService menuGroupService;
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    void 메뉴_그룹을_등록할_수_있다() {
        // given
        final MenuGroup expected = createMenuGroupRequest("메인 메뉴");
        final MenuGroup actual = menuGroupService.create(expected);

        // when & then
        assertThat(actual.getId()).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName())
        );
    }

    @NullAndEmptySource
    @ParameterizedTest
    void 메뉴_그룹의_이름이_존재하지_않으면_등록할_수_없다(final String name) {
        // given & when & then
        final MenuGroup expected = createMenuGroupRequest(name);
        assertThatThrownBy(() -> menuGroupService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 그룹의 이름이 존재해야 합니다.");
    }

    @Test
    void 메뉴_그룹의_목록을_조회할_수_있다() {
        // given
        final MenuGroup expected1 = createMenuGroupRequest("메인 메뉴");
        final MenuGroup expected2 = createMenuGroupRequest("사이드 메뉴");

        // when
        menuGroupService.create(expected1);
        menuGroupService.create(expected2);
        final List<MenuGroup> response = menuGroupService.findAll();

        // then
        assertThat(response).hasSize(2);
     }

    private MenuGroup createMenuGroupRequest(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
