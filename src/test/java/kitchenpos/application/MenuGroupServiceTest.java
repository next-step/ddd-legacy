package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.support.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class MenuGroupServiceTest extends BaseServiceTest {
    private final MenuGroupService menuGroupService;
    private final MenuGroupRepository menuGroupRepository;

    public MenuGroupServiceTest(final MenuGroupService menuGroupService, final MenuGroupRepository menuGroupRepository) {
        this.menuGroupService = menuGroupService;
        this.menuGroupRepository = menuGroupRepository;
    }

    @DisplayName("연관 메뉴는 등록이 가능하다")
    @Test
    void test1() {
        final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();

        final MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

        final MenuGroup foundMenuGroup = menuGroupRepository.findAll().get(0);

        assertThat(createdMenuGroup.getId()).isNotNull();
        assertThat(createdMenuGroup.getName()).isEqualTo(menuGroup.getName());
        assertThat(foundMenuGroup.getId()).isEqualTo(createdMenuGroup.getId());
    }

    @DisplayName("연관 메뉴는 이름으로 등록이 가능하며, 공백이면 안된다.")
    @ParameterizedTest
    @NullAndEmptySource
    void test2(final String name) {
        final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup(name);

        assertThatIllegalArgumentException().isThrownBy(() -> menuGroupService.create(menuGroup));
    }

    @DisplayName("연관 메뉴는 전체 조회가 가능하다.")
    @Test
    void test3() {
        final MenuGroup chicken = menuGroupRepository.save(MenuGroupFixture.createMenuGroup(UUID.randomUUID(), "치킨"));
        final MenuGroup pizza = menuGroupRepository.save(MenuGroupFixture.createMenuGroup(UUID.randomUUID(), "피자"));

        final List<MenuGroup> menus = menuGroupService.findAll();

        assertThat(menus).containsExactly(chicken, pizza);
    }
}