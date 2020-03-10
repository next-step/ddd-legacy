package kitchenpos.builder;

import kitchenpos.model.MenuProduct;

public class MenuProductBuilder {
    private Long seq;
    private Long menuId;
    private Long productId;
    private long quantity;

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

    public MenuProduct build() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setMenuId(menuId);
        menuProduct.setQuantity(quantity);
        menuProduct.setSeq(seq);
        return menuProduct;
    }
}
