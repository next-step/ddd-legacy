package kitchenpos.unit.application;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.testdouble.MenuGroupStubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuGroupServiceTest {

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(new MenuGroupStubRepository());
    }

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void create() {
        // Arrange
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("my menuGroup");

        // Act
        MenuGroup result = menuGroupService.create(menuGroup);

        // Assert
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo("my menuGroup")
        );
    }

    @DisplayName("이름이 없으면 메뉴 그룹을 생성 하지 못한다.")
    @Test
    void without_menu_group_name() {
        // Arrange
        MenuGroup menuGroup = new MenuGroup();

        // Act
        // Assert
        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록된 모든 메뉴 그룹을 조회한다.")
    @Test
    void findAll() {
        // Arrange
        MenuGroup menuGroup1 = createMenuGroupWithName();
        MenuGroup menuGroup2 = createMenuGroupWithName();
        MenuGroup menuGroup3 = createMenuGroupWithName();

        menuGroupService.create(menuGroup1);
        menuGroupService.create(menuGroup2);
        menuGroupService.create(menuGroup3);

        // Act
        List<MenuGroup> all = menuGroupService.findAll();

        // Assert
        assertThat(all).hasSize(3);
    }

    private MenuGroup createMenuGroupWithName() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹 이름");

        return menuGroup;
    }
}
