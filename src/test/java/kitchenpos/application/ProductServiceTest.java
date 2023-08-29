package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.support.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class ProductServiceTest extends BaseServiceTest {
    private final ProductService productService;
    private final ProductRepository productRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    public ProductServiceTest(final ProductService productService, final ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @DisplayName("상품은 등록이 가능하다")
    @Test
    void test1() {
        final Product product = createProduct();
        given(purgomalumClient.containsProfanity(product.getName())).willReturn(false);

        final Product createdProduct = productService.create(product);

        final Product foundProduct = productRepository.findAll().get(0);

        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo(product.getName());
        assertThat(createdProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(foundProduct.getId()).isEqualTo(createdProduct.getId());
    }

    @DisplayName("상품의 이름은 비어있으면 안된다")
    @Test
    void test2() {
        final Product product = createProduct(null, BigDecimal.TEN);
        given(purgomalumClient.containsProfanity(product.getName())).willReturn(false);

        assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품의 이름에 비속어가 포함되면 안된다")
    @Test
    void test3() {
        final Product product = createProduct("비속어", BigDecimal.TEN);

        when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

        assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품은 가격 수정이 가능하다")
    @Test
    void test4() {
        final Product product = productRepository.save(createProduct(UUID.randomUUID(), "상품", BigDecimal.TEN));
        final Product newPriceProduct = createProduct("상품", BigDecimal.ONE);

        final Product changedPriceProduct = productService.changePrice(product.getId(), newPriceProduct);

        assertThat(changedPriceProduct.getId()).isEqualTo(product.getId());
        assertThat(changedPriceProduct.getPrice()).isEqualTo(newPriceProduct.getPrice());
    }

    @DisplayName("상품은 전체 조회가 가능하다")
    @Test
    void test5() {
        final Product chicken = productRepository.save(createProduct(UUID.randomUUID(), "치킨", BigDecimal.ONE));
        final Product pizza = productRepository.save(createProduct(UUID.randomUUID(), "피자", BigDecimal.ZERO));

        final List<Product> products = productRepository.findAll();

        assertThat(products).containsExactly(chicken, pizza);
    }
}
