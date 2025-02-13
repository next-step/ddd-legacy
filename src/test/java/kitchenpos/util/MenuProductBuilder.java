package kitchenpos.util;

import java.util.UUID;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductBuilder {

    private UUID productId;
    private Product product;
    private int quantity;

    private MenuProductBuilder() {
    }

    public static MenuProductBuilder productId(UUID productId) {
        MenuProductBuilder menuProductBuilder = new MenuProductBuilder();
        menuProductBuilder.productId = productId;
        return menuProductBuilder;
    }

    public MenuProductBuilder product(Product product) {
        this.product = product;
        return this;
    }

    public MenuProductBuilder quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public MenuProduct build() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
