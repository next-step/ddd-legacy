package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;

import kitchenpos.domain.Product;
import kitchenpos.fake.menu.TestMenuRepository;
import kitchenpos.fake.product.TestProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductServiceTest {

    @Test
    @DisplayName("상품생성 성공 테스트")
    void create_product_success() {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (name) -> false
        );
        BigDecimal price = BigDecimal.ONE;
        String name = "goodName";
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when
        Product product = productService.create(request);

        // then
        assertAll(
                () -> assertThat(product.getPrice()).isEqualTo(price),
                () -> assertThat(product.getName()).isEqualTo(name),
                () -> assertThat(product.getId()).isNotNull()
        );
    }

    @Test
    @DisplayName("가격은 반드시 존재해야 한다")
    @ValueSource(longs = {-1L})
    void create_product_fail_price1() {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (name) -> false
        );
        BigDecimal price = null;
        String name = "goodName";
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @ParameterizedTest
    @DisplayName("가격은 0보다 작을 수 없다")
    @ValueSource(longs = {-1L})
    void create_product_fail_price2(long negativePrice) {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (name) -> false
        );
        BigDecimal price = BigDecimal.valueOf(negativePrice);
        String name = "goodName";
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }


    @ParameterizedTest
    @DisplayName("이름은 반드시 있어야 한다")
    @NullAndEmptySource
    void create_product_fail_name1(String name) {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (n) -> false
        );
        BigDecimal price = BigDecimal.valueOf(10L);
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @ParameterizedTest
    @DisplayName("이름에 비속어가 포함되면 안된다")
    @ValueSource(strings = {"badname"})
    void create_product_fail_name2(String badName) {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (name) -> true
        );
        BigDecimal price = BigDecimal.valueOf(10L);
        String name = badName;
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }
}
