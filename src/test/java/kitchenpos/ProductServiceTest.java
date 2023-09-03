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
        Product 반반치킨_상품 = createProduct(productId, "반반치킨", BigDecimal.valueOf(16000));
        MenuProduct 반반치킨_메뉴상품 = createMenuProduct(UUID.randomUUID(), 1, 반반치킨_상품);
        Menu 반반치킨_메뉴 = createMenu("반반치킨", BigDecimal.valueOf(16000), UUID.randomUUID(), true, List.of(반반치킨_메뉴상품));

        when(productRepository.findById(productId)).thenReturn(Optional.of(반반치킨_상품));
        when(menuRepository.findAllByProductId(productId)).thenReturn(List.of(반반치킨_메뉴));

        Product request = createProduct("반반치킨", BigDecimal.valueOf(17000));

        // Act
        Product 가격수정된_반반치킨_상품 = productService.changePrice(productId, request);

        // Assert
        assertThat(가격수정된_반반치킨_상품.getPrice()).isEqualTo(BigDecimal.valueOf(17000));
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
        List<Product> 전체상품목록 = new ArrayList<>();
        전체상품목록.add(createProduct("간장치킨", BigDecimal.valueOf(16000)));
        전체상품목록.add(createProduct("순살치킨", BigDecimal.valueOf(15000)));

        when(productRepository.findAll()).thenReturn(전체상품목록);

        // Act
        List<Product> allProducts = productService.findAll();

        // Assert
        assertThat(allProducts).hasSize(2);
    }
}
