package kitchenpos.application.menu;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import kitchenpos.fake.repository.InMemoryMenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class MenuGroupServiceTest {
    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    @DisplayName("메뉴 그룹을 생성한다")
    void createMenuGroup() {
        MenuGroup request = MenuGroupFixture.menuGroup("나의 메뉴 그룹");

        MenuGroup created = menuGroupService.create(request);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("나의 메뉴 그룹");
    }

    @Test
    @DisplayName("메뉴 그룹 목록을 조회한다")
    void findAllMenuGroups() {
        MenuGroup group1 = MenuGroupFixture.menuGroup("메뉴 1");
        MenuGroup group2 = MenuGroupFixture.menuGroup("메뉴 2");
        menuGroupRepository.save(group1);
        menuGroupRepository.save(group2);

        List<MenuGroup> result = menuGroupService.findAll();

        assertThat(result).hasSize(2)
                .extracting("name")
                .contains("메뉴 1", "메뉴 2");
    }

    @DisplayName("이름이 없는 메뉴 그룹은 생성할 수 없다")
    @NullAndEmptySource
    @ParameterizedTest
    void cannotMenuGroupWithoutName(String name) {
        MenuGroup request = MenuGroupFixture.menuGroup(name);

        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
