package kitchenpos.builder;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.Random;
import java.util.UUID;

public final class MenuProductBuilder {
    private Long seq;
    private Product product;
    private long quantity;
    private UUID productId;

    private MenuProductBuilder() {
        seq = new Random().nextLong();
        product = ProductBuilder.newInstance()
                .build();
        quantity = 1L;
        productId = product.getId();
    }

    public static MenuProductBuilder newInstance() {
        return new MenuProductBuilder();
    }

    public MenuProductBuilder setSeq(Long seq) {
        this.seq = seq;
        return this;
    }

    public MenuProductBuilder setProduct(Product product) {
        this.product = product;
        this.productId = product.getId();
        return this;
    }

    public MenuProductBuilder setQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public MenuProductBuilder setProductId(UUID productId) {
        this.productId = productId;
        return this;
    }

    public MenuProduct build() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);
        return menuProduct;
    }
}
