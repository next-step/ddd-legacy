package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);
    private final ProductService productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    private final Product product = new Product();

    @BeforeEach
    void setUp() {
        product.setId(UUID.randomUUID());
    }

    @DisplayName("상품을 등록할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"짜장면:7000", "우동:6000"}, delimiter = ':')
     void create(String name, BigDecimal price) {
        // given
        product.setName(name);
        product.setPrice(price);
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Product result = productService.create(product);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getPrice()).isEqualTo(price);
    }

    @DisplayName("상품 가격은 0원 이상이어야 한다.")
    @ParameterizedTest
    @CsvSource(value = {"짜장면:7000", "우동:6000"}, delimiter = ':')
    void positivePrice(String name, BigDecimal price) {
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(product);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getPrice()).isEqualTo(price);
    }

    @Test
    void changePrice() {
    }

    @Test
    void findAll() {
    }
}