package kitchenpos.product.application;

import kitchenpos.application.FakePurgomalumClient;
import kitchenpos.common.infra.PurgomalumClient;
import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.menu.menu.application.InMemoryMenuRepository;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import kitchenpos.product.dto.request.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("상품 서비스")
class ProductCrudServiceTest {

    private ProductCrudService productCrudService;
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakePurgomalumClient();
        productCrudService = new ProductCrudService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품 목록을 조회할 수 있다.")
    @Test
    void findProducts() {
        productRepository.save(new Product(UUID.randomUUID(), new Name("상품명", false), new Price(BigDecimal.TEN)));
        assertThat(productCrudService.findAll()).hasSize(1);
    }

    @DisplayName("상품 가격은 필수이다.")
    @Test
    void requireProductPrice() {
        ProductRequest request = new ProductRequest(UUID.randomUUID(), "상품명", null);
        assertThatThrownBy(() -> productCrudService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 일 수 없습니다.");
    }

    @DisplayName("상품명은 필수이다.")
    @ParameterizedTest
    @NullAndEmptySource
    void requireProductName(String name) {
        ProductRequest request = new ProductRequest(UUID.randomUUID(), name, BigDecimal.TEN);
        assertThatThrownBy(() -> productCrudService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 이나 공백일 수 없습니다.");
    }

    @DisplayName("상품명은 비속어를 사용할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"비속어", "욕설"})
    void profanity(String name) {
        ProductRequest request = new ProductRequest(UUID.randomUUID(), name, BigDecimal.TEN);
        assertThatThrownBy(() -> productCrudService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비속어를 포함할 수 없습니다.");
    }

    @DisplayName("상품 가격은 0원보다 작을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    void negative(BigDecimal price) {
        ProductRequest request = new ProductRequest(UUID.randomUUID(), "상품명", price);
        assertThatThrownBy(() -> productCrudService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }
}
