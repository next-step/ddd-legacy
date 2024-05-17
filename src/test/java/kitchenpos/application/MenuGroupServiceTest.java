package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.infra.InMemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuGroupServiceTest {

    private InMemoryMenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름이 null이거나 빈 문자열일 경우 메뉴 그룹 생성 시 IllegalArgumentException이 발생한다.")
    void create_fail_for_null_or_empty_name(String name) {
        MenuGroup menuGroup = createMenuGroupRequest(name);

        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹을 생성한다.")
    void create_success() {
        MenuGroup menuGroup = createMenuGroupRequest("메뉴그룹");
        MenuGroup savedGroup = menuGroupService.create(menuGroup);
        assertThat(savedGroup.getId()).isNotNull();
    }

    @Test
    @DisplayName("모든 메뉴 그룹을 조회한다.")
    void findAll() {
        // given
        MenuGroup menuGroupRequest1 = createMenuGroupRequest("메뉴그룹1");
        MenuGroup menuGroupRequest2 = createMenuGroupRequest("메뉴그룹2");
        MenuGroup savedMenuGroup1 = menuGroupService.create(menuGroupRequest1);
        MenuGroup savedMenuGroup2 = menuGroupService.create(menuGroupRequest2);

        // when
        Iterable<MenuGroup> menuGroups = menuGroupService.findAll();

        // then
        assertThat(menuGroups).containsExactlyInAnyOrder(savedMenuGroup1, savedMenuGroup2);
    }

    private MenuGroup createMenuGroupRequest(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}