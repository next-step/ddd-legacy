package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MenuProductTest {
    @DisplayName("메뉴 구성 생성")
    @Test
    void test1() {
        final Long seq = 0L;
        final Product product = createProduct();
        final long quantity = 10;
        final UUID productId = UUID.randomUUID();

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);

        assertThat(menuProduct.getSeq()).isEqualTo(seq);
        assertThat(menuProduct.getProduct()).isEqualTo(product);
        assertThat(menuProduct.getQuantity()).isEqualTo(quantity);
        assertThat(menuProduct.getProductId()).isEqualTo(productId);
    }
}