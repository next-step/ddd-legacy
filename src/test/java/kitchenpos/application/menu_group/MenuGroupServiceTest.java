package kitchenpos.application.menu_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.fake.repository.InMemoryMenuGroupRepository;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MenuGroupServiceTest {

  private MenuGroupService menuGroupService;

  @BeforeEach
  public void init() {
    InMemoryMenuGroupRepository inMemoryMenuGroupRepository = new InMemoryMenuGroupRepository();
    menuGroupService = new MenuGroupService(inMemoryMenuGroupRepository);
  }

  @DisplayName("메뉴그룹을 등록할 수 있다.")
  @Test
  public void register() {
    MenuGroup menuGroup = MenuGroupFixture.normal();
    menuGroup = menuGroupService.create(menuGroup);
    assertThat(menuGroup).isNotNull();
  }

  @DisplayName("메뉴그룹명은 1자 이상이어야한다.")
  @NullAndEmptySource
  @ParameterizedTest
  public void InvalidateMenuGroupName(String name) {
    MenuGroup menuGroup = MenuGroupFixture.create(name);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuGroupService.create(menuGroup));
  }

  @DisplayName("등록된 메뉴그룹 전체를 조회할 수 있다.")
  @Test
  public void InvalidateMenuGroupName() {
    int saveSize = 5;
    List<MenuGroup> requestList = MenuGroupFixture.createList(saveSize);
    for (MenuGroup request : requestList) {
      menuGroupService.create(request);
    }
    List<MenuGroup> menuGroups = menuGroupService.findAll();
    assertThat(menuGroups).hasSize(saveSize);
  }

}
