package kitchenpos;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class KitchenposFixture {

  public static MenuGroup menuGroup() {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName("menu group name");
    return menuGroup;
  }

}
