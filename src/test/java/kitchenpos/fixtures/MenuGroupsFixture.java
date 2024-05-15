package kitchenpos.fixtures;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;

public class MenuGroupsFixture {
  private final List<MenuGroupFixture> menuFixtures;

  public MenuGroupsFixture(List<MenuGroupFixture> menuFixtures) {
    this.menuFixtures = menuFixtures;
  }

  public static MenuGroupsFixture create() {
    return new MenuGroupsFixture(
        Arrays.asList(
            new MenuGroupFixture("추천 메뉴1"),
            new MenuGroupFixture("추천 메뉴2"),
            new MenuGroupFixture("추천 메뉴3"),
            new MenuGroupFixture("추천 메뉴4")));
  }

  public List<MenuGroupFixture> getList() {
    return menuFixtures;
  }
}
