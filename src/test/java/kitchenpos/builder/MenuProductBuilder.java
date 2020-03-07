package kitchenpos.builder;

import kitchenpos.model.MenuProduct;

public class MenuProductBuilder {
    private Long seq;
    private Long menuId;
    private Long productId;
    private long quantity;

    public MenuProductBuilder() {

    }

    public MenuProductBuilder(Long seq, Long menuId, Long productId, long quantity) {
        this.seq = seq;
        this.menuId = menuId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public MenuProductBuilder seq(Long val) {
        seq = val;
        return this;
    }

    public MenuProductBuilder menuId(Long val) {
        menuId = val;
        return this;
    }

    public MenuProductBuilder productId(Long val) {
        productId = val;
        return this;
    }

    public MenuProductBuilder quantity(long val) {
        quantity = val;
        return this;
    }


    public MenuProduct build() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(this.seq);
        menuProduct.setMenuId(this.menuId);
        menuProduct.setProductId(this.productId);
        menuProduct.setQuantity(this.quantity);

        return menuProduct;
    }
}
