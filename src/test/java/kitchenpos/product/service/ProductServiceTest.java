package kitchenpos.product.service;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.product.step.ProductStep.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Product 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품 가격은 0 이상 이어야 한다")
    @Test
    void createWithNegativePrice() {
        // given
        Product product = createProduct("강정치킨", -1);

        // when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름에 욕설을 허용하지 않는다")
    @Test
    void createWithoutBadWords() {
        // given
        Product product = createProduct("fuck", 17000);
        when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경될 상품 가격은 0이상 이어야 한다")
    @Test
    public void changePriceWithNegativePrice() {
        // given
        Product product = createProduct("강정치킨", -1);

        // when, then
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품이 속한 각 메뉴의 가격이 메뉴상품들의 가격의 합보다 크면 숨김 처리한다")
    @Test
    public void changePriceNotDisplay() {
        // given
        Product product = createProduct("후라이드", 16000);
        Menu menu = createMenu("후라이드+후라이드", 33000, Arrays.asList(
                createMenuProduct(product, 1),
                createMenuProduct(product, 1)));
        when(productRepository.findById(any())).thenReturn(Optional.of(createProduct("후라이드", 15000)));

        // when
        Product expectedProduct = productService.changePrice(UUID.randomUUID(), product);

        // then
        assertThat(expectedProduct.getPrice()).isEqualTo(new BigDecimal(16000));
        assertThat(menu.isDisplayed()).isFalse();
    }
}
