package kitchenpos.stub;

import java.util.List;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductStub {

	private static final long DEFAULT_QUANTITY = 1L;

	private MenuProductStub() {
	}

	public static MenuProduct createDefault() {
		return createCustom(ProductStub.createDefault(), DEFAULT_QUANTITY);
	}

	public static MenuProduct createCustom(Product product, long quantity) {
		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setProduct(product);
		menuProduct.setQuantity(quantity);
		return menuProduct;
	}

	public static List<MenuProduct> createDefaultList() {
		return List.of(createDefault(), createDefault());
	}
}
