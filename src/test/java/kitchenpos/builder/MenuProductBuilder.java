package kitchenpos.builder;

import kitchenpos.model.MenuProduct;

public class MenuProductBuilder {
    private Long seq;
    private Long menuId;
    private Long productId;
    private long quantity;

    private MenuProductBuilder() {
    }

    public static MenuProductBuilder menuProduct() {
        return new MenuProductBuilder();
    }

    public MenuProductBuilder withSeq(Long seq) {
        this.seq = seq;
        return this;
    }

    public MenuProductBuilder withMenuId(Long menuId) {
        this.menuId = menuId;
        return this;
    }

    public MenuProductBuilder withProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public MenuProductBuilder withQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public MenuProduct build() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(this.menuId);
        menuProduct.setSeq(this.seq);
        menuProduct.setQuantity(this.quantity);
        menuProduct.setProductId(this.productId);
        return menuProduct;
    }
}
