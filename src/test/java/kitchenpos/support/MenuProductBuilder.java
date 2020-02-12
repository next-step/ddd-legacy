package kitchenpos.support;

import kitchenpos.model.MenuProduct;

public class MenuProductBuilder {
    private long seq;
    private long menuId;
    private long productId;
    private long quantity;

    private MenuProductBuilder() {
    }

    public static MenuProductBuilder menuProduct() {
        return new MenuProductBuilder();
    }

    public MenuProductBuilder withSeq(final long seq) {
        this.seq = seq;
        return this;
    }

    public MenuProductBuilder withMenuId(final long menuId) {
        this.menuId = menuId;
        return this;
    }

    public MenuProductBuilder WithProductId(final long productId) {
        this.productId = productId;
        return this;
    }

    public MenuProductBuilder withQuantity(final long quantity) {
        this.quantity = quantity;
        return this;
    }

    public MenuProduct build() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setMenuId(menuId);
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
