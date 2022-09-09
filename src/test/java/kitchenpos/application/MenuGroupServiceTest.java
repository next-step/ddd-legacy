package kitchenpos.application;

import kitchenpos.FixtureFactory;
import kitchenpos.IntegrationTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuGroupServiceTest extends IntegrationTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Test
    @DisplayName("메뉴분류 기준을 모두 확인할 수 있다.")
    void findAll() {
        List<MenuGroup> menuGroups = new ArrayList<>();
        menuGroups.add(FixtureFactory.createMenuGroup("추천메뉴"));
        menuGroups.add(FixtureFactory.createMenuGroup("인기메뉴"));
        menuGroups.add(FixtureFactory.createMenuGroup("신메뉴"));
        menuGroupRepository.saveAll(menuGroups);

        List<MenuGroup> foundMenuGroups = menuGroupService.findAll();
        assertThat(foundMenuGroups).usingRecursiveComparison().isEqualTo(menuGroups);
    }

    @Test
    @DisplayName("메뉴분류를 성공적으로 만들 수 있다.")
    void create_menu_group() {
        MenuGroup menuGroup = FixtureFactory.createMenuGroup("추천메뉴");
        MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

        MenuGroup foundMenuGroup = menuGroupRepository.findById(createdMenuGroup.getId()).orElseThrow(IllegalArgumentException::new);

        assertThat(foundMenuGroup.getId()).isEqualTo(createdMenuGroup.getId());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴분류를 만들 때 분류명은 비어있을 수 없다.")
    void create_fail(String name) {
        MenuGroup menuGroup = FixtureFactory.createMenuGroup("추천메뉴");
        menuGroup.setName(name);

        assertThatThrownBy(
            () -> menuGroupService.create(menuGroup))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("분류명은 비어있을 수 없습니다.");
    }
}
