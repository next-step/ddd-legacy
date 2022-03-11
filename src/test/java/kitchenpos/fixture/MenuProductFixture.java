package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class MenuProductFixture {

    public static class MenuProductBuilder {
        private Long seq;
        private Product product;
        private long quantity;
        private UUID productId;

        public MenuProductBuilder() { }

        public MenuProductBuilder seq(long seq) {
            this.seq=seq;
            return this;
        }

        public MenuProductBuilder product(Product product) {
            this.product=product;
            return this;
        }

        public MenuProductBuilder quantity(long quantity) {
            this.quantity=quantity;
            return this;
        }

        public MenuProductBuilder productId(UUID productId) {
            this.productId=productId;
            return this;
        }

        public MenuProduct build() {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setSeq(this.seq);
            menuProduct.setProduct(this.product);
            menuProduct.setQuantity(this.quantity);
            menuProduct.setProductId(this.productId);
            return menuProduct;
        }
    }
}
