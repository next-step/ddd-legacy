package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuGroupRepository;
import kitchenpos.application.fake.FakeMenuRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class MenuGroupServiceTest {
    private final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
    private final MenuGroupService service = new MenuGroupService(menuGroupRepository);

    @Test
    @DisplayName("메뉴 그룹은 이름을 입력하여 등록할 수 있다.")
    void create() {
        MenuGroup menuGroup = new MenuGroup("메뉴 그룹");

        MenuGroup savedMenuGroup = service.create(menuGroup);

        assertThat(savedMenuGroup.getId()).isNotNull();
        assertThat(savedMenuGroup.getName()).isEqualTo(menuGroup.getName());
    }

    @ParameterizedTest
    @DisplayName("`메뉴 그룹`의 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    void create_not_empty_name(String name) {
        MenuGroup menuGroup = new MenuGroup(name);

        assertThatThrownBy(() -> service.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("등록한 모든 메뉴 그룹을 조회할 수 있다.")
    void findAll() {

        MenuGroup savedMenuGroup1 = service.create(new MenuGroup("메뉴 그룹1"));
        MenuGroup savedMenuGroup2 = service.create(new MenuGroup("메뉴 그룹2"));
        MenuGroup savedMenuGroup3 = service.create(new MenuGroup("메뉴 그룹3"));

        List<MenuGroup> savedMenuGroups = Lists.list(savedMenuGroup1, savedMenuGroup2, savedMenuGroup3);

        List<MenuGroup> menuGroups = service.findAll();
        assertThat(menuGroups).containsAll(savedMenuGroups);

    }
}