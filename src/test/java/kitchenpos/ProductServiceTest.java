package kitchenpos;

import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.TestFixtureFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @Test
    @DisplayName("상품 등록 - 정상적으로 상품 등록 후 반환")
    void createValidProductReturnsCreatedProduct() {
        // Arrange
        Product request = createProduct("반반치킨", BigDecimal.valueOf(16000));

        when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(new Product());

        // Act
        Product createdProduct = productService.create(request);

        // Assert
        assertThat(createdProduct).isNotNull();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 등록 - 부적절한 이름으로 상품 등록 시 예외 발생")
    void createProductWithProfanityNameThrowsIllegalArgumentException() {
        // Arrange
        Product request = createProduct("Bad Word", BigDecimal.valueOf(16000));

        when(purgomalumClient.containsProfanity(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("가격 수정 - 정상적으로 가격 수정 후 반환")
    void changePriceValidProductReturnsUpdatedProduct() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product existingProduct = createProduct(productId, "반반치킨", BigDecimal.valueOf(16000));
        MenuProduct menuProduct = createMenuProduct(UUID.randomUUID(), 1, existingProduct);
        Menu menu = createMenu("반반치킨", BigDecimal.valueOf(16000), UUID.randomUUID(), true, List.of(menuProduct));

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(menuRepository.findAllByProductId(productId)).thenReturn(List.of(menu));

        Product request = createProduct("반반치킨", BigDecimal.valueOf(17000));

        // Act
        Product updatedProduct = productService.changePrice(productId, request);

        // Assert
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(17000));
    }

    @Test
    @DisplayName("가격 수정 - 가격이 음수일 때 예외 발생")
    void changePriceNegativePriceThrowsIllegalArgumentException() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product request = createProduct("반반치킨", BigDecimal.valueOf(-5000));

        // Act & Assert
        assertThatThrownBy(() -> productService.changePrice(productId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("전체 상품 조회")
    void findAllProductsReturnsAllProducts() {
        // Arrange
        List<Product> products = new ArrayList<>();
        products.add(createProduct("간장치킨", BigDecimal.valueOf(16000)));
        products.add(createProduct("순살치킨", BigDecimal.valueOf(15000)));

        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> allProducts = productService.findAll();

        // Assert
        assertThat(allProducts).hasSize(2);
    }
}
