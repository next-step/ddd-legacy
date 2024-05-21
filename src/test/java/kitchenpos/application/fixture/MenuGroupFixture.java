package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupFixture {
  public static MenuGroup create(String name) {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setName(name);
    return menuGroup;
  }

  public static MenuGroup createFake(String name) {
    MenuGroup menuGroup = create(name);
    menuGroup.setId(UUID.randomUUID());
    return menuGroup;
  }
}
