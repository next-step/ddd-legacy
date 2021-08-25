package kitchenpos.application.old;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OldMenuGroupServiceTest {

    private final String menuGroupName = "세트류";

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("메뉴그룹 생성")
    @Test
    void create() {
        MenuGroup menuGroupRequest = new MenuGroup(menuGroupName);
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);
        assertThat(menuGroup).isNotNull();
    }

    @DisplayName("메뉴그룹 생성시 메뉴그룹 이름 null, empty validation")
    @NullAndEmptySource
    @ParameterizedTest
    void createValidationMenuName(String name) {
        MenuGroup request = new MenuGroup(name);
        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("모든 메뉴그룹 조회")
    @Test
    void findAll() {
        MenuGroup menuGroupRequest = new MenuGroup(menuGroupName);
        menuGroupService.create(menuGroupRequest);

        List<MenuGroup> menuGroups = menuGroupRepository.findAll();
        assertThat(menuGroups.size()).isEqualTo(menuGroupService.findAll().size());
    }
}