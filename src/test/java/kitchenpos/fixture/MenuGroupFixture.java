package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

  public static final String FOR_TWO_NAME = "이인용";
  public static MenuGroup FOR_TWO = createMenuGroup(FOR_TWO_NAME);

  public static MenuGroup createMenuGroup(final String name) {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName(name);
    return menuGroup;
  }
}
