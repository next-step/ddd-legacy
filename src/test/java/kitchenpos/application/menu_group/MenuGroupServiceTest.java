package kitchenpos.application.menu_group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import kitchenpos.application.MenuGroupService;
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
    MenuGroupFakeRepository menuGroupFakeRepository = new MenuGroupFakeRepository();
    menuGroupService = new MenuGroupService(menuGroupFakeRepository);
  }

  @DisplayName("메뉴그룹을 등록할 수 있다.")
  @Test
  public void register() {
    MenuGroup request = new MenuGroup();
    request.setName("메뉴그룹");
    MenuGroup menuGroup = menuGroupService.create(request);
    assertThat(menuGroup).isNotNull();
  }

  @DisplayName("메뉴그룹명이 1자 미만일 경우 IllegalArgumentException 예외 처리를 한다.")
  @NullAndEmptySource
  @ParameterizedTest
  public void InvalidateMenuGroupName(String name) {
    MenuGroup request = new MenuGroup();
    request.setName(name);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuGroupService.create(request));
  }

  @DisplayName("등록된 메뉴그룹 전체를 조회할 수 있다.")
  @Test
  public void InvalidateMenuGroupName() {
    MenuGroup menuGroup1 = new MenuGroup();
    menuGroup1.setName("그룹1");
    MenuGroup menuGroup2 = new MenuGroup();
    menuGroup2.setName("그룹2");

    menuGroupService.create(menuGroup1);
    menuGroupService.create(menuGroup2);
    List<MenuGroup> menuGroups = menuGroupService.findAll();

    assertThat(menuGroups).hasSize(2);
  }

}
