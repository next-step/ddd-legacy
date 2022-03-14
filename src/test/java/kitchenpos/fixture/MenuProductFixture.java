package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.Collections;
import java.util.List;

import static kitchenpos.fixture.ProductFixture.*;

public class MenuProductFixture {
  private static final long QUANTITY = 1L;
  private static final long MINUS_QUANTITY = -1L;

  public static MenuProduct 정상_메뉴_품목_가격_만원() {
    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setQuantity(QUANTITY);

    Product product = 상품_가격_만원();
    menuProduct.setProductId(product.getId());
    menuProduct.setProduct(product);

    return menuProduct;
  }

  public static MenuProduct 메뉴_품목_수량_음수() {
    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setQuantity(MINUS_QUANTITY);

    Product product = 상품_가격_만원();
    menuProduct.setProductId(product.getId());
    menuProduct.setProduct(product);

    return menuProduct;
  }

  public static MenuProduct 정상_메뉴_품목_가격_이만원() {
    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setQuantity(QUANTITY);

    Product product = 상품_가격_이만원();
    menuProduct.setProductId(product.getId());
    menuProduct.setProduct(product);

    return menuProduct;
  }

  public static List<MenuProduct> 상품_품목_리스트_가격_만원() {
    return Collections.singletonList(정상_메뉴_품목_가격_만원());
  }


  public static List<MenuProduct> 상품_품목_리스트_가격_이만원() {
    return Collections.singletonList(정상_메뉴_품목_가격_이만원());
  }
  public static List<MenuProduct> 상품_품목_리스트_수량_음수() {
    return Collections.singletonList(메뉴_품목_수량_음수());
  }

}
