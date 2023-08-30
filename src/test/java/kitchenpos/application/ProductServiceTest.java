package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.Fixtures.메뉴_생성;
import static kitchenpos.Fixtures.상품_메뉴;
import static kitchenpos.Fixtures.상품_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProductServiceTest {

    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakePurgomalumClient();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {
        final Product expected = 상품_생성("치킨", 16_000L);
        final Product actual = productService.create(expected);
        assertThat(actual).isNotNull();
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice())
        );
    }

    @DisplayName("상품의 이름이 올바르지 않으면 등록할 수 없다.")
    @ValueSource(strings = {"비속어", "개새끼"})
    @NullSource
    @ParameterizedTest
    void createWithEmptyName(String name) {
        final Product expected = 상품_생성(name, 16_000L);
        assertThatThrownBy(() -> productService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격을 변경 할 수 있다.")
    @Test
    void changePriceProduct() {
        final UUID productId = productRepository.save(상품_생성("치킨", 16_000L)).getId();
        final Product 상품_생성 = 상품_생성(15_000L);
        final Product 응답_결과 = productService.changePrice(productId, 상품_생성);
        assertThat(응답_결과.getPrice()).isEqualTo(상품_생성.getPrice());
    }

    @DisplayName("존재 하지 않는 상품의 가격을 변경 할 수 없다.")
    @Test
    void changePriceProductWithNotExistProduct() {
        final UUID productId = UUID.randomUUID();
        final Product 상품_생성 = 상품_생성(15_000L);
        assertThatThrownBy(() -> productService.changePrice(productId, 상품_생성))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("상품의 목록을 조회할 수 있다.")
    @Test
    void list() {
        final Product 상품_생성 = 상품_생성("치킨", 16_000L);
        productRepository.save(상품_생성);
        final Product 상품_생성2 = 상품_생성("피자", 15_000L);
        productRepository.save(상품_생성2);
        final List<Product> 상품_목록 = productService.findAll();
        assertThat(상품_목록).hasSize(2);
    }

    @DisplayName("메뉴 가격은 상품 가격 * 수량의 총합보다 작거나 같지 않으면 메뉴를 숨긴다 (노출 중지)")
    @Test
    void hideMenu() {
        final Product 상품 = productRepository.save(상품_생성("치킨", 16_000L));
        final Menu 메뉴 = menuRepository.save(메뉴_생성(19_000L, true, 상품_메뉴(상품, 2L)));
        productService.changePrice(상품.getId(), 상품_생성(9_000L));
        assertThat(menuRepository.findById(메뉴.getId()).get().isDisplayed()).isFalse();
    }
}