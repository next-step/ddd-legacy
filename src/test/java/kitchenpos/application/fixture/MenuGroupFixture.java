package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {

  public static MenuGroup createMenuGroup() {
    return createMenuGroup("추천메뉴");
  }

  public static MenuGroup createMenuGroup(String name) {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName(name);

    return menuGroup;
  }
}
