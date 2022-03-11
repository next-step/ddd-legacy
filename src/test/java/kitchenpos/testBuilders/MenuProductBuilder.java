package kitchenpos.testBuilders;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.UUID;

import static kitchenpos.testBuilders.ProductBuilder.aDefaultProduct;

public class MenuProductBuilder {
	public static final long DEFAULT_SEQ = 1L;
	public static final long DEFAULT_QUANTITY = 1L;
	private long seq;
	private Product product;
	private long quantity;
	private UUID productId;

	private MenuProductBuilder() {
	}

	public static MenuProductBuilder aMenuProduct() {
		return new MenuProductBuilder();
	}

	public static MenuProductBuilder aDefaultMenuProduct() {
		Product defaultProduct = aDefaultProduct().build();
		return aMenuProduct()
				.withSeq(DEFAULT_SEQ)
				.withProduct(defaultProduct)
				.withQuantity(DEFAULT_QUANTITY)
				.withProductId(defaultProduct.getId());
	}

	public MenuProductBuilder withSeq(long seq) {
		this.seq = seq;
		return this;
	}

	public MenuProductBuilder withProduct(Product product) {
		this.product = product;
		return this;
	}

	public MenuProductBuilder withQuantity(long quantity) {
		this.quantity = quantity;
		return this;
	}

	public MenuProductBuilder withProductId(UUID productId) {
		this.productId = productId;
		return this;
	}

	public MenuProduct build() {
		MenuProduct menuProduct = new MenuProduct();
		menuProduct.setSeq(seq);
		menuProduct.setProduct(product);
		menuProduct.setProductId(productId);
		menuProduct.setQuantity(quantity);
		return menuProduct;
	}
}
