package kitchenpos.model;

public class MenuProduct {
    private Long seq;
    private Long menuId;
    private Long productId;
    private long quantity;

    public Long getSeq() {
        return seq;
    }

    public void setSeq(final Long seq) {
        this.seq = seq;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(final Long menuId) {
        this.menuId = menuId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }


    public static final class MenuProductBuilder {

        private Long seq;
        private Long menuId;
        private Long productId;
        private long quantity;

        private MenuProductBuilder() {
        }

        public static MenuProductBuilder builder() {
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
            menuProduct.setSeq(seq);
            menuProduct.setMenuId(menuId);
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(quantity);
            return menuProduct;
        }
    }
}
