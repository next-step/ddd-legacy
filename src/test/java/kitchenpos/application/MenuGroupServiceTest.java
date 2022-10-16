package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuGroupServiceTest {

    private final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
    private final MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);

    @Test
    @DisplayName("메뉴 그룹을 생성하여 저장한다.")
    void createMenuGroup() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴 그룹");

        // when
        MenuGroup createMenuGroup = menuGroupService.create(menuGroup);

        // then
        assertAll(
                () -> assertThat(createMenuGroup.getId()).isNotNull(),
                () -> assertThat(createMenuGroup.getName()).isEqualTo("메뉴 그룹")
        );
    }

    @Test
    @DisplayName("메뉴 그룹 리스트를 가져온다.")
    void findMenuGroups() {
        // given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴 그룹");
        MenuGroup menuGroup2 = new MenuGroup();
        menuGroup2.setId(UUID.randomUUID());
        menuGroup2.setName("메뉴 그룹2");
        menuGroupService.create(menuGroup);
        menuGroupService.create(menuGroup2);

        // when
        // then
        List<MenuGroup> menuGroups = menuGroupService.findAll();
        assertAll(
                () -> assertThat(menuGroups).isNotEmpty(),
                () -> assertThat(menuGroups.size()).isEqualTo(2)
        );
    }
}
