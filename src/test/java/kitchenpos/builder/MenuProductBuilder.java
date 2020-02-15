package kitchenpos.builder;

import kitchenpos.model.MenuProduct;

public class MenuProductBuilder {
    private MenuProduct menuProduct;
    private Long seq;
    private Long menuId;
    private Long productId;
    private long quantity;

    private MenuProductBuilder() {
        this.menuProduct = new MenuProduct();
    }

    public static MenuProductBuilder create() {
        return new MenuProductBuilder();
    }

    public MenuProduct build() {
        return this.menuProduct;
    }

    public MenuProductBuilder setSeq(Long seq) {
        this.seq = seq;
        return this;
    }

    public MenuProductBuilder setMenuId(Long menuId) {
        this.menuId = menuId;
        return this;
    }

    public MenuProductBuilder setProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public MenuProductBuilder setQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }
}
