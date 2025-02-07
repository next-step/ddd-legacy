package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.mock.fixture.MenuGroupFixture;
import kitchenpos.mock.persistence.FakeMenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

class MenuGroupServiceTest {

    private final FakeMenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
    private final MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);

    @DisplayName("정해진 이름으로 메뉴그룹을 생성할 수 있다.")
    @Test
    void create() {
        // given
        String name = "추천 메뉴";
        MenuGroup menuGroup = MenuGroupFixture.create(name);

        // when
        MenuGroup savedMenuGroup = menuGroupService.create(menuGroup);

        // then
        assertThat(savedMenuGroup.getName()).isEqualTo(menuGroup.getName());
        assertThat(savedMenuGroup.getId()).isNotNull();
    }

    @DisplayName("이름 없이 메뉴그룹을 생성 시, 예외가 발생한다.")
    @ParameterizedTest
    @NullSource
    @EmptySource
    void createNameException(String name) {
        // given
        MenuGroup menuGroup = MenuGroupFixture.create(name);

        // when then
        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("저장된 메뉴그룹 리스트를 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        List<String> names = List.of("추천 메뉴", "오늘의 메뉴", "할인 메뉴", "세트 메뉴");
        names.forEach(name -> {
            MenuGroup menuGroup = MenuGroupFixture.create(name);
            menuGroupService.create(menuGroup);
        });

        // when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        // then
        assertThat(menuGroups)
            .hasSize(names.size())
            .extracting(MenuGroup::getName)
            .containsExactlyInAnyOrder(names.toArray(String[]::new));
    }
}
