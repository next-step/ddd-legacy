package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.ArrayList;
import java.util.List;

import static kitchenpos.KitchenposFixture.ID;

public class MenuGroupFixture {
  private static final String MENU_GROUP = "menu group name";

  public static MenuGroup 정상_메뉴_그룹() {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(ID);
    menuGroup.setName(MENU_GROUP);
    return menuGroup;
  }

  public static List<MenuGroup> 정상_메뉴_그룹_리스트() {
    List<MenuGroup> menuGroupList = new ArrayList<>();
    menuGroupList.add(정상_메뉴_그룹());
    return menuGroupList;
  }
}
