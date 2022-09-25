package kitchenpos.application;

import kitchenpos.FixtureFactory;
import kitchenpos.IntegrationTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProductServiceTest extends IntegrationTest {

    private static final Product PRODUCT = FixtureFactory.createProduct("후라이드", BigDecimal.valueOf(16000));

    @Test
    void find_all_product_test() {
        List<Product> productList = productRepository.saveAll(List.of(
            FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(17000)),
            FixtureFactory.createProduct("후라이드 치킨", BigDecimal.valueOf(16000)),
            FixtureFactory.createProduct("간장 치킨", BigDecimal.valueOf(18000)))
        );

        List<Product> foundProducts = productService.findAll();

        assertThat(foundProducts).usingRecursiveComparison().ignoringFields("price").isEqualTo(productList);
        // price cannot be compared due to big decimal precision
    }

    @Nested
    @DisplayName("제품을 만들 때")
    class CreateTest {

        @Test
        @DisplayName("성공적으로 만든다.")
        void create_product_test() {
            PRODUCT.setName("name");
            PRODUCT.setPrice(BigDecimal.ONE);
            Product createdProduct = productService.create(PRODUCT);

            assertAll(
                () -> assertThat(createdProduct.getName()).isEqualTo(PRODUCT.getName()),
                () -> assertThat(createdProduct.getPrice()).isEqualTo(PRODUCT.getPrice())
            );
        }

        @Test
        @DisplayName("가격이 음수일 수 없다.")
        void create_negative_price_product_test() {
            Product product = FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(16000));
            product.setPrice(BigDecimal.valueOf(-1));

            assertThatThrownBy(
                () -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("제품명이 공백이거나 비속어일 수 없다.")
        void create_nullable_name_product_test(String name) {
            Product product = FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(16000));
            product.setName(name);

            assertThatThrownBy(
                () -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제품명은 공백이거나 비속어가 포함될 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("제품 가격을 변경할 때 ")
    class ChangePriceTest {

        @Test
        @DisplayName("성공적으로 변경한다.")
        void changePrice() {
            Product savedProduct = productRepository.save(PRODUCT);
            BigDecimal priceToChange = BigDecimal.TEN;
            savedProduct.setPrice(priceToChange);
            productService.changePrice(savedProduct.getId(), savedProduct);

            Product changedProduct = productRepository.findById(savedProduct.getId()).orElseThrow(IllegalArgumentException::new);

            assertThat(changedProduct.getPrice().compareTo(priceToChange)).isZero();
        }

        @Test
        @DisplayName("가격은 음수일 수 없다.")
        void change_negative_price() {
            Product savedProduct = productRepository.save(PRODUCT);
            savedProduct.setPrice(BigDecimal.valueOf(-10));

            assertThatThrownBy(
                () -> productService.changePrice(savedProduct.getId(), savedProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 음수일 수 없습니다.");
        }

        @Test
        @DisplayName("메뉴의 가격이 제품의 총합보다 크면, 고객에게 가려진다 ")
        void change_price_to_higher() {
            MenuGroup menuGroup = menuGroupRepository.save(FixtureFactory.createMenuGroup("추천메뉴"));

            Product savedProduct = productRepository.save(PRODUCT);
            Menu menu = menuRepository.save(FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(16000), true, menuGroup, toMenuProductList(List.of(savedProduct))));

            savedProduct.setPrice(BigDecimal.valueOf(10000));
            productService.changePrice(savedProduct.getId(), savedProduct);

            Menu hiddenMenu = menuRepository.findById(menu.getId()).orElseThrow(IllegalArgumentException::new);
            assertFalse(hiddenMenu.isDisplayed());
        }
    }
}
