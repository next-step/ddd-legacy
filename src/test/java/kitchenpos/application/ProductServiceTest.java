package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.helper.MenuGroupHelper;
import kitchenpos.helper.MenuHelper;
import kitchenpos.helper.ProductHelper;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;
import static kitchenpos.helper.NameHelper.NAME_OF_255_CHARACTERS;
import static kitchenpos.helper.ProductHelper.DEFAULT_PRICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductServiceTest extends BaseServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuService menuService;

    @SpyBean
    private PurgomalumClient purgomalumClient;


    @BeforeEach
    void beforeEach() {
        when(purgomalumClient.containsProfanity(any()))
                .thenReturn(false);
    }


    @DisplayName("새로운 상품을 등록한다.")
    @Nested
    class CreateProduct {

        @DisplayName("상품에 대한 가격은 0원 이상이어야 한다.")
        @Nested
        class Policy1 {
            @DisplayName("상품에 대한 가격은 0원 이상인 경우 (성공)")
            @ParameterizedTest
            @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
            void success1(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);
                Product product = ProductHelper.create(price);

                // When
                Product createdProduct = productService.create(product);

                // Then
                assertThat(createdProduct.getPrice()).isEqualTo(price);
            }

            @DisplayName("상품에 대한 가격은 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(BigDecimal price) {
                // When
                Product product = ProductHelper.create(price);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("상품에 대한 가격은 0원 미만인 경우 (실패)")
            @ParameterizedTest
            @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
            void fail2(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);

                // When
                Product product = ProductHelper.create(price);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("상품명은 비어있을 수 없고, 255자를 초과할 수 없다.")
        @Nested
        class Policy2 {
            @DisplayName("상품명이 0자 이상 255자 이하인 경우 (성공)")
            @ParameterizedTest
            @ValueSource(strings = {"", "한", "a", "1", "상품명", "product name", "상품 A", NAME_OF_255_CHARACTERS})
            void success1(final String name) {
                // Given
                Product product = ProductHelper.create(name);

                // When
                Product createdProduct = productService.create(product);

                // Then
                assertThat(createdProduct.getName()).isEqualTo(name);
                assertThat(createdProduct.getPrice()).isEqualTo(DEFAULT_PRICE);
            }

            @DisplayName("상품명이 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(final String name) {
                // When
                Product product = ProductHelper.create(name);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("상품명에 비속어가 포함되어 있으면 안 된다.")
        @Nested
        class Policy3 {
            @DisplayName("상품명이 비속어인 경우 (실패)")
            @ParameterizedTest
            @ValueSource(strings = {"나쁜놈", "fuck"})
            void fail1(final String name) {
                // Given
                when(purgomalumClient.containsProfanity(name)).thenReturn(true);

                // When
                Product product = ProductHelper.create(name);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

    }

    @DisplayName("기존 상품의 가격을 변경한다.")
    @Nested
    class ChangeProductPrice {

        private Product beforeCreatedProduct;
        private Menu displayedMenu;

        @BeforeEach
        void beforeEach() {
            beforeCreatedProduct = productService.create(ProductHelper.create());
            MenuGroup createdMenuGroup = menuGroupService.create(MenuGroupHelper.create());

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setSeq(1L);
            menuProduct.setProductId(beforeCreatedProduct.getId());
            menuProduct.setProduct(beforeCreatedProduct);
            menuProduct.setQuantity(10);
            BigDecimal menuPrice = menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity()));
            Menu createdMenu = menuService.create(MenuHelper.create(menuPrice, createdMenuGroup.getId(), List.of(menuProduct)));
            displayedMenu = menuService.display(createdMenu.getId());
        }

        @DisplayName("상품에 대한 가격은 0원 이상이어야 한다.")
        @Nested
        class Policy1 {
            @DisplayName("상품에 대한 가격은 0원 이상인 경우 (성공)")
            @ParameterizedTest
            @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
            void success1(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);
                beforeCreatedProduct.setPrice(price);

                // When
                Product createdProduct = productService.changePrice(beforeCreatedProduct.getId(), beforeCreatedProduct);

                // Then
                assertThat(createdProduct.getPrice()).isEqualTo(price);
            }

            @DisplayName("상품에 대한 가격은 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(BigDecimal price) {
                // Given
                beforeCreatedProduct.setPrice(price);

                // When
                assertThatThrownBy(() -> productService.changePrice(beforeCreatedProduct.getId(), beforeCreatedProduct))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("상품에 대한 가격은 0원 미만인 경우 (실패)")
            @ParameterizedTest
            @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
            void fail2(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);

                // When
                beforeCreatedProduct.setPrice(price);

                // Then
                assertThatThrownBy(() -> productService.changePrice(beforeCreatedProduct.getId(), beforeCreatedProduct))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("해당 상품이 등록된 모든 메뉴에 대해서, 변경된 상품 가격을 토대로 메뉴의 가격 조건을 검증한다. 만약 조건을 만족하지 못 했을 때는, 해당 메뉴는 숨김 처리한다.")
        @Nested
        class Policy2 {
            @DisplayName("메뉴의 가격 조건을 만족하지 않았을 경우, 메뉴 숨김 처리 (성공)")
            @ParameterizedTest
            @ValueSource(ints = {1, 10, 100})
            void success1(final int price) {
                // Given
                BigDecimal minimumProductPrice = displayedMenu.getMenuProducts().parallelStream()
                        .map(menuProduct -> menuProduct.getProduct().getPrice())
                        .min(Comparator.naturalOrder())
                        .orElse(BigDecimal.ZERO);

                BigDecimal changedProductPrice = minimumProductPrice.subtract(BigDecimal.valueOf(price));

                // When
                Product changedProduct = productService.changePrice(beforeCreatedProduct.getId(), ProductHelper.create(beforeCreatedProduct.getName(), changedProductPrice));

                // Then
                assertThat(changedProduct.getName()).isEqualTo(beforeCreatedProduct.getName());
                assertThat(changedProduct.getPrice()).isEqualTo(changedProductPrice);
                assertThat(displayedMenu.isDisplayed()).isEqualTo(false);
            }
        }
    }

    @DisplayName("모든 상품을 가져온다.")
    @Nested
    class FindAllProducts {

        private List<Product> beforeCreatedProducts;

        @BeforeEach
        void beforeEach() {
            beforeCreatedProducts = IntStream.range(0, 11)
                    .mapToObj(n -> productService.create(ProductHelper.create()))
                    .collect(toUnmodifiableList());
        }

        @DisplayName("모든 상품을 가져온다 (성공)")
        @Test
        void success1() {
            // When
            List<Product> allProducts = productService.findAll();

            // Then
            assertThat(allProducts.size()).isEqualTo(beforeCreatedProducts.size());
        }
    }

}