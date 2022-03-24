package kitchenpos;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static kitchenpos.TestFixtures.createMenuGroup;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuGroupServiceTest {

    private final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();

    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴그룹1");


        // when
        final MenuGroup result = menuGroupService.create(menuGroup);

        // then
        assertAll(
                () -> Assertions.assertThat(result.getId()).isNotNull(),
                () -> Assertions.assertThat(result.getName()).isEqualTo("메뉴그룹1")
        );
    }

    @DisplayName("메뉴 그룹 목록 이름은 비어있지 않아야 한다.")
    @Test
    void createWithEmptyName() {
        // given
        final MenuGroup menuGroup = new MenuGroup();


        // when - then
        Assertions.assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 그룹 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        MenuGroup menuGroup1 = createMenuGroup(UUID.randomUUID(), "메뉴그룹1");
        menuGroupRepository.save(menuGroup1);
        MenuGroup menuGroup2 = createMenuGroup(UUID.randomUUID(), "메뉴그룹2");
        menuGroupRepository.save(menuGroup2);


        // when
        List<MenuGroup> result = menuGroupService.findAll();

        // then
        Assertions.assertThat(result.size()).isEqualTo(2);
    }


}
