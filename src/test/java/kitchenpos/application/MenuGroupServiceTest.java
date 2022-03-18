package kitchenpos.application;

import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuGroupTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MenuGroupServiceTest {

    public static MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void create() {
        MenuGroup createdMenuGroup = menuGroupService.create(MenuGroupTest.create("1인 메뉴"));

        assertThat(createdMenuGroup).isNotNull();
    }

    @DisplayName("메뉴그룹의 목록을 조회한다.")
    @Test
    void findAll() {
        saveMenuGroup(MenuGroupTest.create("한마리 치킨"));
        saveMenuGroup(MenuGroupTest.create("두마리 치킨"));
        int menuGroupSize = menuGroupRepository.findAll()
                .size();

        List<MenuGroup> findedMenuGroup = menuGroupService.findAll();

        assertThat(findedMenuGroup).hasSize(menuGroupSize);
    }

    @DisplayName("메뉴그룹의 이름은 null이 될수 없다.")
    @Test
    void createMenuGroupExceptionNullCase() {
        MenuGroup menuGroup = MenuGroupTest.create(null);
        assertThatThrownBy(
                () -> menuGroupService.create(menuGroup)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴그룹의 이름은 공백이 될수 없다.")
    @Test
    void createMenuGroupExceptionEmptyCase() {
        MenuGroup menuGroup = MenuGroupTest.create("");
        assertThatThrownBy(
                () -> menuGroupService.create(menuGroup)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    public static MenuGroup saveMenuGroup(MenuGroup menuGroup) {
        return menuGroupRepository.save(menuGroup);
    }
}
