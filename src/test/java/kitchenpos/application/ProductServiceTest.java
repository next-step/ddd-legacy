package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product")
class ProductServiceTest {

    @Mock
    private PurgomalumClient purgomalumClient;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final String PRODUCT_NAME = "name";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(10);
    private Product product;
    private MenuProduct menuProduct;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        product.setPrice(PRODUCT_PRICE);

        menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
    }

    @Test
    @DisplayName("상품은 식별키, 이름, 가격을 가진다.")
    void test1() {
        assertAll(
                () -> assertThat(product.getId()).isEqualTo(PRODUCT_ID),
                () -> assertThat(product.getName()).isEqualTo(PRODUCT_NAME),
                () -> assertThat(product.getPrice()).isEqualTo(PRODUCT_PRICE)
        );
    }

    @Test
    @DisplayName("상품을 등록한다.")
    void test2() {
        // Given
        when(productRepository.save(any())).thenReturn(product);

        // When
        Product result = productService.create(product);

        // Then
        assertThat(result).isEqualTo(product);
    }

    @Nested
    @DisplayName("상품의 가격은 비어있거나 0보다 작을 수 없다.")
    class test3 {
        @Test
        @DisplayName("가격은 비어있을 수 없다.")
        void test1() {
            // Given
            Product product = new Product();

            // When
            product.setId(PRODUCT_ID);
            product.setName(PRODUCT_NAME);
            product.setPrice(null);

            // Then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("가격은 0보다 작을 수 없다.")
        void test2() {
            // Given
            Product product = new Product();

            // When
            product.setId(PRODUCT_ID);
            product.setName(PRODUCT_NAME);
            product.setPrice(BigDecimal.valueOf(-1));

            // Then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("상품의 이름은 없거나 비속어 이면 안된다.")
    class test4 {
        @Test
        @DisplayName("이름은 비어있을 수 없다.")
        void test1() {
            // Given
            Product product = new Product();

            // When
            product.setId(PRODUCT_ID);
            product.setName(null);
            product.setPrice(PRODUCT_PRICE);

            // Then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("이름은 비속어 일 수 없다.")
        void test2() {
            // Given
            Product product = new Product();

            // When
            when(purgomalumClient.containsProfanity("비속어")).thenReturn(true);
            product.setId(PRODUCT_ID);
            product.setPrice(PRODUCT_PRICE);
            product.setName("비속어");

            // Then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("상품의 전체목록을 조회할 수 있다.")
    void test5() {
        // Given
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<Product> findAllProducts = productService.findAll();

        // Then
        assertThat(findAllProducts.size()).isEqualTo(2);
    }

    @Nested
    @DisplayName("상품의 가격을 변경할 수 있다.")
    class test6 {
        @Test
        @DisplayName("상품 가격 변경이 성공적으로 이루어진 경우")
        void test1() {
            // Given
            when(productRepository.findById(eq(PRODUCT_ID))).thenReturn(Optional.of(product));

            // When
            Product changedProduct = productService.changePrice(PRODUCT_ID, product);

            // Then
            assertThat(changedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(10));
        }

        @Test
        @DisplayName("가격은 비어있을 수 없다.")
        void test2() {
            // Given
            Product product = new Product();

            // When
            product.setId(PRODUCT_ID);
            product.setName(PRODUCT_NAME);
            product.setPrice(null);

            // Then
            assertThatThrownBy(() -> productService.changePrice(PRODUCT_ID, product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("가격은 0보다 작을 수 없다.")
        void test3() {
            // Given
            Product product = new Product();

            // When
            product.setId(PRODUCT_ID);
            product.setName(PRODUCT_NAME);
            product.setPrice(BigDecimal.valueOf(-1));

            // Then
            assertThatThrownBy(() -> productService.changePrice(PRODUCT_ID, product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("존재하는 상품이어야 한다.")
        void test4() {
            when(productRepository.findById(eq(PRODUCT_ID))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.changePrice(PRODUCT_ID, product))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Nested
        @DisplayName("가격이 바뀐 후 메뉴의 가격이 메뉴에 속한 상품들의 수량 * 가격 보다 크면 노출하지 않는다.")
        class test5 {
            @Test
            @DisplayName("노출하지 않는 경우")
            void test1() {
                // Given
                Menu menu = new Menu();
                menu.setPrice(BigDecimal.valueOf(300));
                menu.setDisplayed(true);
                menu.setMenuProducts(Collections.singletonList(menuProduct));

                when(productRepository.findById(eq(PRODUCT_ID))).thenReturn(Optional.of(product));
                when(menuRepository.findAllByProductId(eq(PRODUCT_ID))).thenReturn(Collections.singletonList(menu));

                // When
                productService.changePrice(PRODUCT_ID, product);

                // Then
                assertThat(menu.isDisplayed()).isFalse();
            }

            @Test
            @DisplayName("노출 하는 경우")
            void test2() {
                // Given
                Menu menu = new Menu();
                menu.setPrice(BigDecimal.valueOf(1));
                menu.setDisplayed(true);
                menu.setMenuProducts(Collections.singletonList(menuProduct));

                when(productRepository.findById(eq(PRODUCT_ID))).thenReturn(Optional.of(product));
                when(menuRepository.findAllByProductId(eq(PRODUCT_ID))).thenReturn(Collections.singletonList(menu));

                // When
                productService.changePrice(PRODUCT_ID, product);

                // Then
                assertThat(menu.isDisplayed()).isTrue();
            }
        }

    }

}