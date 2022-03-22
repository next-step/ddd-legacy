package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.exception.MenuGroupNameException;
import kitchenpos.inMemory.InMemoryMenuGroupRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuGroupServiceTest {

    private static MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() throws Exception {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹은 반드시 고유 ID와 그룹명을 갖는다.")
    @Test
    void create() {

        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("세트메뉴");

        // when
        final MenuGroup actual = menuGroupService.create(menuGroup);

        // then
        Assertions.assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo("세트메뉴")
        );
    }

    @DisplayName("등록한 모든 메뉴 그룹 목록을 볼 수 있다.")
    @Test
    void findAll() {
        // given
        menuGroupRepository.save(createMenuGroup("세트메뉴"));
        menuGroupRepository.save(createMenuGroup("사이드메뉴"));

        // when
        final List<MenuGroup> listMenuGroup = menuGroupService.findAll();

        // then
        assertThat(listMenuGroup).hasSize(2);
    }

    @DisplayName("메뉴 그룹은 반드시 이름을 갖는다.")
    @Test
    void createMenuGroupNameException() {
        final MenuGroup menuGroup = createMenuGroup(null);
        System.out.println(menuGroup);
        assertThatThrownBy(
                () -> menuGroupService.create(menuGroup)
        ).isInstanceOf(MenuGroupNameException.class);
    }

    static MenuGroup createMenuGroup(String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroupRepository.save(menuGroup);
    }

}
