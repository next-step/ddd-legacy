package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.test.util.ReflectionTestUtils;

public class MenuFixture {
  public static MenuProduct createMenuProduct(Product product, Long quantity, Long seq) {
    MenuProduct menuProduct = new MenuProduct();
    ReflectionTestUtils.setField(menuProduct, "product", product);
    ReflectionTestUtils.setField(menuProduct, "quantity", quantity);
    ReflectionTestUtils.setField(menuProduct, "productId", product.getId());
    ReflectionTestUtils.setField(menuProduct, "seq", seq);
    return menuProduct;
  }

  public static Menu createMenu(String name, MenuGroup menuGroup, List<MenuProduct> menuProducts, Long price, boolean displayed) {
    Menu menu = new Menu();
    ReflectionTestUtils.setField(menu, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(menu, "name", name);
    ReflectionTestUtils.setField(menu, "menuGroup", menuGroup);
    ReflectionTestUtils.setField(menu, "menuProducts", menuProducts);
    ReflectionTestUtils.setField(menu, "price", new BigDecimal(price));
    ReflectionTestUtils.setField(menu, "displayed", displayed);
    ReflectionTestUtils.setField(menu, "menuGroupId", menuGroup.getId());
    return menu;
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
