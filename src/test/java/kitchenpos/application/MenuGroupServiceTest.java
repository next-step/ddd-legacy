package kitchenpos.application;

import static org.mockito.BDDMockito.*;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixtures.FixtureMenu;
import kitchenpos.infra.menu.InMemoryMenuGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

  private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
  private final MenuGroupService menuGroupService;

  public MenuGroupServiceTest() {
    this.menuGroupService = new MenuGroupService(menuGroupRepository);
  }

  @Test
  @DisplayName("메뉴 그룹을 등록하기 위해 메뉴 그룹 이름 입력해야 한다.")
  void case1() {
    final MenuGroup menuGroup = FixtureMenu.fixtureMenuGroup();
    given(menuGroupRepository.save(any())).willReturn(menuGroup);

    final MenuGroup resultMenuGroup = menuGroupService.create(menuGroup);
    Assertions.assertThat(menuGroup.getName()).isEqualTo(resultMenuGroup.getName());
  }

  @Test
  @DisplayName("메뉴 그룹을 전체 조회할 수 있다.")
  void case2() {
    given(menuGroupRepository.findAll())
        .willReturn(List.of(FixtureMenu.fixtureMenuGroup()));

    final List<MenuGroup> all = menuGroupService.findAll();
    Assertions.assertThat(1).isEqualTo(all.size());
  }
}
