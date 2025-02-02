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
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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


    @DisplayName("상품 가격은 0원보다 작다면 에러를 발생시킨다.")
    @ParameterizedTest
    @CsvSource(value = {"짜장면:-1", "우동:-3000"}, delimiter = ':')
    void minusPrice(String name, BigDecimal price) {
        product.setName(name);
        product.setPrice(price);
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품명은 비어있다면 에러를 발생시킨다.")
    @ParameterizedTest
    @NullSource
    void nullName(String name) {
        product.setName(name);
        product.setPrice(BigDecimal.ONE);
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격이 비어있다면 에러를 발생시킨다.")
    @ParameterizedTest
    @NullSource
    void nullPrice(BigDecimal price) {
        product.setName("짜장면");
        product.setPrice(price);
        // Mock 객체가 save() 호출 시 product를 반환하도록 설정
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changePrice() {
    }

    @Test
    void findAll() {
    }
}