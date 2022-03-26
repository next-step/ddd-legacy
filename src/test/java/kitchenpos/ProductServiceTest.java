package kitchenpos;

import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static kitchenpos.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProductServiceTest {

    private final ProductRepository productRepository = new FakeProductRepository();
    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final ProfanityClient purgomalumClient = new FakeProfanityClient();

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final Product product = createProductRequest("후라이드", 18_000);

        // when
        final Product result = productService.create(product);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo("후라이드"),
                () -> assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(18_000))
        );
    }

    @DisplayName("상품의 가격은 음수로 등록할 수 있다.")
    @ValueSource(ints = {-1, -100, -10000})
    @ParameterizedTest
    void createWithNegativePrice(final int price) {
        // given
        final Product product = createProductRequest(price);

        // when - then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 이름에는 비속어가 포함될 수 없다.")
    @ValueSource(strings = {"욕설", "비속어"})
    @ParameterizedTest
    void createWithBadName(final String name) {
        // given
        final Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(18_000L));

        // when - then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("상품의 가격을 수정할 수 있다.")
    @Test
    void update() {

        //given
        final Product product = createProduct("후라이드", 18_000);
        productRepository.save(product);

        final Product updateProductRequest = updateProductRequest(product, 17_000);

        // when
        final Product result = productService.changePrice(product.getId(), updateProductRequest);

        // then
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(17_000));

    }

    @DisplayName("상품의 가격이 음수라면 수정할 수 없다.")
    @ValueSource(ints = {-1, -100, -10000})
    @ParameterizedTest
    void updateWithNegativePrice(int price) {

        //given
        final Product product = createProduct("후라이드", 18_000);
        productRepository.save(product);

        final Product updateProductRequest = updateProductRequest(product, price);

        // when - then
        assertThatThrownBy(() -> productService.changePrice(product.getId(), updateProductRequest))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("상품을 수정할때는 상품이 이미 등록된 상태여야 한다.")
    @Test
    void updateWithNoneId() {
        //given
        final Product product = createProduct("후라이드", 18_000);

        final Product updateProductRequest = updateProductRequest(product, 17_000);

        // when - then
        assertThatThrownBy(() -> productService.changePrice(product.getId(), updateProductRequest))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("해당 상품이 속한 메뉴의 가격이 상품 가격의 합보다 크다면 메뉴가 표시되지 않는다.")
    @Test
    void hasSamePrice() {
        //given
        final Product product = createProduct("후라이드", 18_000);
        productRepository.save(product);

        final Menu menu = createMenuRequest(18_000);
        MenuProduct menuProduct = createMenuProduct(product, 1L);
        menu.setMenuProducts(new ArrayList<>());
        menu.getMenuProducts().add(menuProduct);
        menuRepository.save(menu);

        final Product updateProductRequest = updateProductRequest(product, 17_000);

        // when
        productService.changePrice(product.getId(), updateProductRequest);

        // then
        assertThat(menu.isDisplayed()).isFalse();

    }

    @DisplayName("상품을 조회한다.")
    @Test
    void findAll() {
        // given
        productRepository.save(createProduct("후라이드", 18_000));
        productRepository.save(createProduct("양념치킨", 19_000));

        final List<Product> actual = productService.findAll();

        // when - then
        assertThat(actual).hasSize(2);

    }

}