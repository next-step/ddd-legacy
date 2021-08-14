package kitchenpos.application;

import static kitchenpos.application.fixture.MenuFixture.CHEAP_PRODUCT_MENU;
import static kitchenpos.application.fixture.MenuFixture.CHEAP_PRODUCT_MENU2;
import static kitchenpos.application.fixture.ProductFixture.CHEAP_PRODUCT;
import static kitchenpos.application.fixture.ProductFixture.CHEAP_PRODUCT_REQUEST;
import static kitchenpos.application.fixture.ProductFixture.PRICE_NEGATIVE_PRODUCT;
import static kitchenpos.application.fixture.ProductFixture.PRICE_NEGATIVE_PRODUCT_REQUEST;
import static kitchenpos.application.fixture.ProductFixture.PRICE_NULL_PRODUCT;
import static kitchenpos.application.fixture.ProductFixture.PRICE_NULL_PRODUCT_REQUEST;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT1;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT1_REQUEST;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT2;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT_WITH_NAME_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductServiceTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;

    private final ProductRepository productRepository = new InmemoryProductRepository();
    private final MenuRepository menuRepository = new InmemoryMenuRepository();
    private final PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("create - 상품을 추가할 수 있다")
    @Test
    void create() {
        //given
        final Product product = PRODUCT1();
        final Product productRequest = PRODUCT1_REQUEST();

        //when
        final Product sut = productService.create(productRequest);

        //then
        assertAll(
            () -> assertThat(sut.getId()).isNotNull(),
            () -> assertThat(sut.getPrice()).isEqualTo(product.getPrice()),
            () -> assertThat(sut.getName()).isEqualTo(product.getName())
        );
    }

    @DisplayName("create - 상품가격이 없으면 예외를 반환한다")
    @Test
    void createEmptyPrice() {
        //given
        final Product productRequest = PRICE_NULL_PRODUCT_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(productRequest));
    }

    @DisplayName("create - 상품가격이 음수면 예외를 반환한다")
    @Test
    void createNegativePrice() {
        //given
        final Product productRequest = PRICE_NEGATIVE_PRODUCT_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(productRequest));
    }

    @DisplayName("create - 상품 이름이 한글자 미만이면 예외를 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void createNegativePrice(final String name) {
        //given
        final Product productRequest = PRODUCT_WITH_NAME_REQUEST(name);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(productRequest));
    }

    @DisplayName("create - 상품 이름에 비속어가 포함되어 있으면 예외를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "damn", "bitch"})
    void createProfanity(final String profanityWord) {
        //given
        final Product productRequest = PRODUCT_WITH_NAME_REQUEST(profanityWord);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(productRequest));
    }

    @DisplayName("changePrice - 상품의 가격을 수정할 수 있다")
    @Test
    void changePrice() {
        //given
        final Product product = productRepository.save(PRODUCT1());
        final Product productRequest = PRODUCT1_REQUEST();

        //when
        final Product sut = productService.changePrice(product.getId(), productRequest);

        //then
        assertThat(sut.getPrice()).isEqualTo(productRequest.getPrice());
    }

    @DisplayName("changePrice - 상품가격이 없으면 예외를 반환한다")
    @Test
    void changePriceNoPrice() {
        //given
        final Product product = productRepository.save(PRICE_NULL_PRODUCT());
        final Product productRequest = PRICE_NULL_PRODUCT_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.changePrice(product.getId(), productRequest));
    }

    @DisplayName("changePrice - 상품가격이 음수면 예외를 반환한다")
    @Test
    void changePriceNegativePrice() {
        //given
        final Product product = productRepository.save(PRICE_NEGATIVE_PRODUCT());
        final Product productRequest = PRICE_NEGATIVE_PRODUCT_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.changePrice(product.getId(), productRequest));
    }

    @DisplayName("changePrice - 존재하는 상품이 아니라면 예외를 반환한다")
    @Test
    void changePriceExist() {
        //given
        final Product product = PRODUCT1();
        final Product productRequest = PRODUCT1_REQUEST();

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> productService.changePrice(product.getId(), productRequest));
    }

    @DisplayName("changePrice - 상품가격 수정으로 인해 메뉴가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸진다면 해당 메뉴를 노출시키지 말아야 한다")
    @Test
    void changePriceMenuDisplay() {
        //given
        final Product product = productRepository.save(CHEAP_PRODUCT());
        final Product productRequest = CHEAP_PRODUCT_REQUEST();

        menuRepository.save(CHEAP_PRODUCT_MENU());
        menuRepository.save(CHEAP_PRODUCT_MENU2());

        //when
        productService.changePrice(product.getId(), productRequest);
        final List<Menu> menus = menuRepository.findAll();

        //then
        assertAll(
            () -> assertThat(menus.get(ZERO)
                .isDisplayed()).isFalse(),
            () -> assertThat(menus.get(ONE)
                .isDisplayed()).isFalse()
        );
    }

    @DisplayName("findAll - 상품 리스트를 조회할 수 있다")
    @Test
    void findAll() {
        //given
        final Product product1 = productRepository.save(PRODUCT1());
        final Product product2 = productRepository.save(PRODUCT2());

        //when
        final List<Product> products = productService.findAll();

        //then
        assertAll(
            () -> assertThat(products).hasSize(TWO),
            () -> assertThat(products.get(ZERO)
                .getName()).isEqualTo(product1.getName()),
            () -> assertThat(products.get(ZERO)
                .getPrice()).isEqualTo(product1.getPrice()),
            () -> assertThat(products.get(ONE)
                .getName()).isEqualTo(product2.getName()),
            () -> assertThat(products.get(ONE)
                .getPrice()).isEqualTo(product2.getPrice())
        );
    }

}
