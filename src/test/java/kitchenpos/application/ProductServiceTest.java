package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.dummy.DummyProduct;
import kitchenpos.exception.ProductNameException;
import kitchenpos.exception.ProductPriceException;
import kitchenpos.fake.FakeProfanityClient;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryProductRepository;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;


public class ProductServiceTest {

    private ProductRepository productRepository = new InMemoryProductRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private ProfanityClient purgomalumClient = new FakeProfanityClient();
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 등록한다.")
    @Test
    void create() {
        final Product request = DummyProduct.createProductRequest();
        Product actual = productService.create(request);
        assertThat(actual).isNotNull();
    }


    @DisplayName("상품의 가격은 0원 미만이면 예외가 발생한다.")
    @Test
    void changePrice_over_0() {
        final Product request = DummyProduct.createProductRequest(-1L );
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(ProductPriceException.class);

    }

    @DisplayName("상품의 이름에는 비속어가 포함될 수 없다.")
    @ValueSource(strings = {"비속어", "욕설", "욕"})
    @ParameterizedTest
    void 상품의_이름에는_비속어가_포함될_수_없다(final String name) {
        final Product request = DummyProduct.createProductRequest(name);
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(ProductNameException.class);

    }

    @DisplayName("상품의 가격을 변경한다.")
    @Test
    void changePrice() {
        final Product request = DummyProduct.createProductRequest(16_000L);
        productRepository.save(request);
        final Product actual = productService.changePrice(request.getId(), DummyProduct.createProductRequest(20_000L));
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(20_000L));
    }

    @DisplayName("상품 목록을 조회한다.")
    @Test
    void findAll() {
        productRepository.save(DummyProduct.createProductRequest(UUID.randomUUID(), BigDecimal.valueOf(16_000L), "후라이드 치킨"));
        productRepository.save(DummyProduct.createProductRequest(UUID.randomUUID(), BigDecimal.valueOf(16_000L), "양념 치킨"));
        final List<Product> actual = productService.findAll();
        assertThat(actual).hasSize(2);
    }

}