package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class MenuGroupServiceTest {
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(new TestMenuGroupRepository());
    }

    @Test
    @DisplayName("메뉴 그룹을 생성한다.")
    void create() {
        String name = "메뉴1";
        MenuGroup request = createMenuGroup(name);

        MenuGroup menuGroup = menuGroupService.create(request);

        assertThat(menuGroup.getId()).isNotNull();
        assertThat(menuGroup.getName()).isEqualTo(name);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴 그룹명은 비어있으면 안된다.")
    void create_valid_name(String name) {
        MenuGroup request = createMenuGroup(name);

        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴그룹을 전체 조회한다.")
    void findAll() {
        menuGroupService.create(createMenuGroup("메뉴1"));
        menuGroupService.create(createMenuGroup("메뉴2"));

        List<MenuGroup> menuGroups = menuGroupService.findAll();

        assertThat(menuGroups).hasSize(2);
    }

    private MenuGroup createMenuGroup(String name) {
        MenuGroup request = new MenuGroup();
        request.setName(name);
        return request;
    }
}
