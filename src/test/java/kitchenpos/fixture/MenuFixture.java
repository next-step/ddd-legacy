package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static kitchenpos.KitchenposFixture.ID;
import static kitchenpos.fixture.MenuGroupFixture.정상_메뉴_그룹;
import static kitchenpos.fixture.MenuProductFixture.*;

public class MenuFixture {

  private static final String MENU_NAME = "menu name";
  private static final BigDecimal PRICE_10000 = BigDecimal.valueOf(10000L);
  private static final BigDecimal PRICE_20000 = BigDecimal.valueOf(20000L);

  private static Menu 메뉴() {
    Menu menu = new Menu();
    menu.setId(ID);
    menu.setName(MENU_NAME);
    menu.setDisplayed(true);

    MenuGroup menuGroup = 정상_메뉴_그룹();
    menu.setMenuGroup(menuGroup);
    menu.setMenuGroupId(menuGroup.getId());
    return menu;
  }

  public static Menu 정상_메뉴_가격_만원() {
    Menu menu = 메뉴();
    menu.setMenuProducts(상품_품목_리스트_가격_만원());
    menu.setPrice(PRICE_10000);
    return menu;
  }


  public static Menu 정상_메뉴_가격_이만원() {
    Menu menu = 메뉴();
    menu.setMenuProducts(상품_품목_리스트_가격_이만원());
    menu.setPrice(PRICE_20000);
    return menu;
  }

  public static List<Menu> 메뉴_리스트_가격_만원() {
    return Collections.singletonList(정상_메뉴_가격_만원());
  }

  public static List<Menu> 메뉴_리스트_가격_이만원() {
    return Collections.singletonList(정상_메뉴_가격_이만원());
  }

}
