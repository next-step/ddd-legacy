package kitchenpos.application;

import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static kitchenpos.domain.MenuGroupTest.createMenu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void create() {
        String menuName = "1인 메뉴";
        MenuGroup createdMenuGroup = menuGroupService.create(createMenu(menuName));

        assertAll(
                () -> assertThat(createdMenuGroup).isNotNull(),
                () -> assertThat(createdMenuGroup.getId()).isNotNull(),
                () -> assertThat(createdMenuGroup.getName()).isEqualTo(menuName)
        );
    }

    @DisplayName("메뉴그룹의 목록을 조회한다.")
    @Test
    void findAll() {
        saveMenuGroup(createMenu("한마리 치킨"));
        saveMenuGroup(createMenu("두마리 치킨"));

        List<MenuGroup> findedMenuGroup = menuGroupService.findAll();

        assertThat(findedMenuGroup).hasSize(2);
    }

    @DisplayName("메뉴그룹의 이름은 null 또는 공백이 될수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createMenuGroupExceptionNullCase(String groupName) {
        MenuGroup menuGroup = createMenu(groupName);
        assertThatThrownBy(
                () -> menuGroupService.create(menuGroup)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    public MenuGroup saveMenuGroup(MenuGroup menuGroup) {
        return menuGroupRepository.save(menuGroup);
    }
}
