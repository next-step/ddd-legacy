package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.fake.menuGroup.TestMenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MenuGroupServiceTest {
    @Test
    @DisplayName("메뉴그룹을 생성할 수 있다")
    void create_menu_group() {
        MenuGroupService menuGroupService = new MenuGroupService(new TestMenuGroupRepository());
        String menuGroupName = "TestMenuGroup";
        MenuGroup request = new MenuGroup();
        request.setName(menuGroupName);
        MenuGroup menuGroup = menuGroupService.create(request);

        assertAll(
                () -> assertThat(menuGroup.getId()).isNotNull(),
                () -> assertThat(menuGroup.getName()).isEqualTo(menuGroupName)
        );
    }

    @ParameterizedTest
    @DisplayName("메뉴그룹이름은 존재해야하며 길이가 1 이상이다")
    @NullSource
    void create_menu_group_fail(String menuGroupName) {
        MenuGroupService menuGroupService = new MenuGroupService(new TestMenuGroupRepository());
        MenuGroup request = new MenuGroup();
        request.setName(menuGroupName);

        assertThrows(IllegalArgumentException.class, () -> menuGroupService.create(request));
    }

    @Test
    @DisplayName("메뉴그룹을 조회할 수 있다")
    void find_menu_group() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(new TestMenuGroupRepository());
        String menuGroupName = "TestMenuGroup";
        MenuGroup request = new MenuGroup();
        request.setName(menuGroupName);
        menuGroupService.create(request);

        // when
        List<MenuGroup> result = menuGroupService.findAll();

        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(1),
                () -> assertThat(result.get(0).getName()).isEqualTo(menuGroupName)
        );
    }
}
