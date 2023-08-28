package kitchenpos.domain;

import kitchenpos.Fixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MenuProductTest {

    @Test
    @DisplayName("메뉴상품은 상품과, 수량을 가지고 있다.")
    void create() {
        Product product = Fixtures.createProduct("상품", new BigDecimal("10000"));
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1L);

        assertThat(menuProduct.getSeq()).isEqualTo(1L);
        assertThat(menuProduct.getProduct().getId()).isEqualTo(product.getId());
        assertThat(menuProduct.getQuantity()).isEqualTo(1L);
    }
}
