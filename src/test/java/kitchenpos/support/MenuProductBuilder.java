package kitchenpos.support;

import kitchenpos.model.MenuProduct;

public class MenuProductBuilder {
    private Long seq;
    private Long menuId;
    private Long productId;
    private long quantity;

    public MenuProductBuilder seq(Long seq) {
        this.seq = seq;
        return this;
    }

    public MenuProductBuilder menuId(Long menuId) {
        this.menuId = menuId;
        return this;
    }

    public MenuProductBuilder productId(Long productId) {
        this.productId = productId;
        return this;
    }

    public MenuProductBuilder quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public MenuProduct build() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(this.seq);
        menuProduct.setMenuId(this.menuId);
        menuProduct.setProductId(this.productId);
        menuProduct.setQuantity(this.quantity);

        return menuProduct;
    }
}
