package kitchenpos.fixture;

import static kitchenpos.fixture.ProductFixture.TWENTY_THOUSANDS;
import static kitchenpos.fixture.ProductFixture.TWO_UDONS;

import kitchenpos.application.product.FakeUuidBuilder;
import kitchenpos.common.UuidBuilder;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public class MenuFixture {
  public static Product udon = ProductFixture.udon;
  public static MenuGroup menuGroup = MenuGroupFixture.FOR_TWO;
  public static Menu menu = MenuFixture.createMenu(TWO_UDONS, TWENTY_THOUSANDS, menuGroup, udon, 2);

  public static Menu createMenu(final String name, final long price, final MenuGroup menuGroup, final Product product, final int quantity) {
    Menu menu = new Menu(new FakeUuidBuilder());
    menu.setName(name);
    menu.setPrice(BigDecimal.valueOf(price));
    menu.setMenuGroupId(menuGroup.getId());
    menu.setDisplayed(true);

    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setProduct(product);
    menuProduct.setQuantity(quantity);
    menuProduct.setProductId(product.getId());

    menu.setMenuProducts(List.of(menuProduct));

    return menu;
  }

  public static Menu createMenu(final UuidBuilder fakeBuilder, final String name, final long price, final MenuGroup menuGroup, final Product product, final int quantity) {
    Menu menu = new Menu(fakeBuilder);
    menu.setName(name);
    menu.setPrice(BigDecimal.valueOf(price));
    menu.setMenuGroupId(menuGroup.getId());
    menu.setDisplayed(true);

    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setProduct(product);
    menuProduct.setQuantity(quantity);
    menuProduct.setProductId(product.getId());

    menu.setMenuProducts(List.of(menuProduct));

    return menu;
  }

}
