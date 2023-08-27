package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenuWithPrice;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @Nested
    @DisplayName("상품을 등록할 수 있다.")
    class create {

        @Test
        @DisplayName("등록")
        void create_1() {
            // Given
            Product product = createProduct();
            when(productRepository.save(any())).thenReturn(product);

            // When
            Product result = productService.create(product);

            // Then
            assertThat(result).isEqualTo(product);
        }

        @Nested
        @DisplayName("가격은 비어있거나 0보다 작으면 예외가 발생한다.")
        class create_2 {

            @Test
            @DisplayName("비어있는 경우")
            void create_2_1() {
                // When
                Product product = createProductWithPrice(null);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("0보다 작은경우")
            void create_2_2() {
                // When
                Product product = createProductWithPrice(BigDecimal.valueOf(-1));

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        @DisplayName("이름은 없거나 비속어 이면 예외가 발생한다.")
        class create_3 {

            @Test
            @DisplayName("비어있는 경우")
            void create_3_1() {
                // When
                Product product = createProductWithName(null);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("비속어인 경우")
            void create_3_2() {
                // When
                when(purgomalumClient.containsProfanity("비속어")).thenReturn(true);
                Product product = createProductWithName("비속어");

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Test
    @DisplayName("상품의 전체목록을 조회할 수 있다.")
    void findAll() {
        // Given
        List<Product> products = createProducts();
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<Product> findAllProducts = productService.findAll();

        // Then
        assertThat(findAllProducts).hasSize(products.size());
    }

    @Nested
    @DisplayName("상품의 가격을 변경할 수 있다.")
    class changePrice {

        @Test
        @DisplayName("변경")
        void changePrice_1() {
            // Given
            Product product = createProduct();
            when(productRepository.findById(any())).thenReturn(Optional.of(product));

            // When
            Product changedProduct = productService.changePrice(any(), product);

            // Then
            assertThat(changedProduct.getPrice()).isEqualTo(BigDecimal.TEN);
        }

        @Nested
        @DisplayName("가격은 비어있거나 0보다 작으면 예외가 발생한다.")
        class changePrice_2 {

            @Test
            @DisplayName("비어있는 경우")
            void changePrice_2_1() {
                // When
                Product product = createProductWithPrice(null);

                // Then
                assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), product))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("0보다 작은경우")
            void changePrice_2_2() {
                // When
                Product product = createProductWithPrice(BigDecimal.valueOf(-1));

                // Then
                assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), product))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("미리 존재하는 상품이 아니면 예외가 발생한다.")
        void changePrice_3() {
            // Given
            Product product = createProduct();

            // When
            when(productRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> productService.changePrice(any(), product))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Nested
        @DisplayName("가격이 바뀐 후 메뉴의 가격이 메뉴에 속한 상품들의 수량 * 가격 보다 크면 노출하지 않는다.")
        class changePrice_4 {

            @Test
            @DisplayName("노출하지 않는 경우")
            void changePrice_4_1() {
                // Given
                Menu menu = createMenuWithPrice(BigDecimal.valueOf(11));
                Product product = createProduct();

                when(productRepository.findById(any())).thenReturn(Optional.of(product));
                when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

                // When
                productService.changePrice(any(), product);

                // Then
                assertThat(menu.isDisplayed()).isFalse();
            }

            @Test
            @DisplayName("노출 하는 경우")
            void changePrice_4_2() {
                // Given
                Menu menu = createMenuWithPrice(BigDecimal.valueOf(9));
                Product product = createProduct();

                when(productRepository.findById(any())).thenReturn(Optional.of(product));
                when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

                // When
                productService.changePrice(any(), product);

                // Then
                assertThat(menu.isDisplayed()).isTrue();
            }
        }
    }

}