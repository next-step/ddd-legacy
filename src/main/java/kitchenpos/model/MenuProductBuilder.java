package kitchenpos.model;

public final class MenuProductBuilder {
    private Long seq;
    private Long menuId;
    private Long productId;
    private long quantity;

    private MenuProductBuilder() {}

    public static MenuProductBuilder aMenuProduct() { return new MenuProductBuilder(); }

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
        menuProduct.setSeq(seq);
        menuProduct.setMenuId(menuId);
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
