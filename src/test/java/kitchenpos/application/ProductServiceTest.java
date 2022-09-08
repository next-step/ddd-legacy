package kitchenpos.application;

import kitchenpos.FixtureFactory;
import kitchenpos.IntegrationTest;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductServiceTest extends IntegrationTest {

    @Test
    void find_all_product_test() {
        List<Product> productList = new ArrayList<>();
        productList.add(FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(16000)));
        productList.add(FixtureFactory.createProduct("후라이드 치킨", BigDecimal.valueOf(16000)));
        productList.add(FixtureFactory.createProduct("간장 치킨", BigDecimal.valueOf(16000)));

        productRepository.saveAll(productList);
        List<Product> foundProducts = productService.findAll();
        assertThat(foundProducts.size()).isEqualTo(productList.size());
    }

    @Nested
    @DisplayName("제품을 만들 때")
    class CreateTest {

        @Test
        @DisplayName("성공적으로 만든다.")
        void create_product_test() {
            Product product = FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(16000));
            product.setName("name");
            product.setPrice(BigDecimal.ONE);
            Product createdProduct = productService.create(product);
            assertThat(createdProduct.getName()).isEqualTo(product.getName());
            assertThat(createdProduct.getPrice()).isEqualTo(product.getPrice());
        }

        @Test
        @DisplayName("가격이 음수일 수 없다.")
        void create_negative_price_product_test() {
            Product product = FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(16000));
            product.setPrice(BigDecimal.valueOf(-1));

            assertThatThrownBy(
                () -> productService.create(product)
            ).isInstanceOf(IllegalArgumentException.class);

        }

        @ParameterizedTest
        @NullSource
        @DisplayName("제품명이 공백이거나 비속어일 수 없다.")
        void create_nullable_name_product_test(String name) {
            Product product = FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(16000));
            product.setName(name);

            assertThatThrownBy(
                () -> productService.create(product)
            ).isInstanceOf(IllegalArgumentException.class);

        }
    }

    @Nested
    @DisplayName("제품 가격을 변경할 때 ")
    class ChangePriceTest {

        @Test
        @DisplayName("성공적으로 변경한다.")
        void changePrice() {
            Product product = FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(16000));
            Product savedProduct = productRepository.save(product);
            BigDecimal priceToChange = BigDecimal.TEN;
            savedProduct.setPrice(priceToChange);
            productService.changePrice(savedProduct.getId(), savedProduct);

            Product changedProduct = productRepository.findById(savedProduct.getId()).orElseThrow(IllegalArgumentException::new);

            assertThat(changedProduct.getPrice().compareTo(priceToChange)).isZero();
        }

        @Test
        @DisplayName("가격은 음수일 수 없다.")
        void change_negative_price() {
            Product product = FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(16000));
            Product savedProduct = productRepository.save(product);
            savedProduct.setPrice(BigDecimal.valueOf(-10));

            assertThatThrownBy(
                () -> productService.changePrice(savedProduct.getId(), savedProduct)
            ).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("가격이 인상되면, 고객에게 가려진다 ")
        void change_higher_price() {

        }
    }


}
