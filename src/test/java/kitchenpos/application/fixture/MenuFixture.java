package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixture {
  public static MenuProduct createMenuProduct(Product product, Long quantity, Long seq) {
    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setProduct(product);
    menuProduct.setQuantity(quantity);
    menuProduct.setProductId(product.getId());
    menuProduct.setSeq(seq);
    return menuProduct;
  }

  public static Menu createMenu(String name, MenuGroup menuGroup, List<MenuProduct> menuProducts, Long price, boolean displayed) {
    Menu request = new Menu();
    request.setId(UUID.randomUUID());
    request.setName(name);
    request.setMenuGroup(menuGroup);
    request.setMenuProducts(menuProducts);
    request.setPrice(new BigDecimal(price));
    request.setDisplayed(displayed);
    request.setMenuGroupId(menuGroup.getId());
    return request;
  }

  public static Menu createDefaultMenu(boolean displayed) {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    Product product = ProductFixture.create("상품", 300L);
    MenuProduct menuProduct = createMenuProduct(product, 3L, 1L);
    return createMenu("기본메뉴", menuGroup, List.of(menuProduct), 200L,  displayed);
  }

  public static Menu createDefaultMenu() {
    return createDefaultMenu(true);
  }
}
