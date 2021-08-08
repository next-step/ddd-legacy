package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ProductServiceTest extends MockTest {

    public static final String PRODUCT_NAME = "상품1";
    public static final long OVER_PRICE = 80001L;
    public static final long NORMAL_PRICE = 80000L;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("create - 상품을 추가할 수 있다")
    @Test
    void create() {
        //given
        final Product product = createProduct(PRODUCT_NAME, 1000L);

        given(productRepository.save(any())).willReturn(product);

        //when
        final Product sut = productService.create(product);

        //then
        assertThat(sut).isInstanceOf(Product.class);
    }

    @DisplayName("create - 상품가격이 없으면 예외를 반환한다")
    @Test
    void createEmptyPrice() {
        //given
        final Product product = createProduct(PRODUCT_NAME, 1000L);
        product.setPrice(null);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("create - 상품가격이 음수면 예외를 반환한다")
    @Test
    void createNegativePrice() {
        //given
        final Product product = createProduct(PRODUCT_NAME, -1000L);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("create - 상품 이름이 한글자 미만이면 예외를 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void createNegativePrice(final String value) {
        //given
        final Product product = createProduct(value, 1000L);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("create - 상품 이름에 비속어가 포함되어 있으면 예외를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "damn", "bitch"})
    void createProfanity(final String profanityWord) {
        //given
        final Product product = createProduct(profanityWord, 1000L);

        given(purgomalumClient.containsProfanity(profanityWord)).willReturn(true);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("changePrice - 상품의 가격을 수정할 수 있다")
    @Test
    void changePrice() {
        //given
        final Product product = createProduct(PRODUCT_NAME, 1000L);

        final Menu menu1 = createMenu("메뉴1", 80001L);
        final Menu menu2 = createMenu("메뉴2", 80000L);
        final List<Menu> menus = Arrays.asList(menu1, menu2);

        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(any())).willReturn(menus);

        //when
        final Product sut = productService.changePrice(product.getId(), product);

        //then
        assertThat(sut).isInstanceOf(Product.class);
    }

    @DisplayName("changePrice - 상품가격이 없으면 예외를 반환한다")
    @Test
    void changePriceNoPrice() {
        //given
        final Product product = createProduct(PRODUCT_NAME, 1000L);
        product.setPrice(null);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.changePrice(product.getId(), product));
    }

    @DisplayName("changePrice - 상품가격이 음수면 예외를 반환한다")
    @Test
    void changePriceNegativePrice() {
        //given
        final Product product = createProduct(PRODUCT_NAME, -1000L);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productService.changePrice(product.getId(), product));
    }

    @DisplayName("changePrice - 존재하는 상품이 아니라면 예외를 반환한다")
    @Test
    void changePriceExist() {
        //given
        final Product product = createProduct(PRODUCT_NAME, 1000L);

        given(productRepository.findById(any())).willThrow(NoSuchElementException.class);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> productService.changePrice(product.getId(), product));
    }

    @DisplayName("changePrice - 상품가격 수정으로 인해 메뉴가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 같거나 비싸진다면 해당 메뉴를 노출시키지 말아야 한다")
    @Test
    void changePriceMenuDisplay() {
        //given
        final Product product = createProduct(PRODUCT_NAME, OVER_PRICE);

        final Menu menu1 = createMenu("메뉴1", OVER_PRICE);
        final Menu menu2 = createMenu("메뉴2", NORMAL_PRICE);
        final List<Menu> menus = Arrays.asList(menu1, menu2);

        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(any())).willReturn(menus);

        //when
        productService.changePrice(product.getId(), product);

        //then
        assertAll(
            () -> assertThat(menus.get(0)
                .isDisplayed()).isFalse(),
            () -> assertThat(menus.get(1)
                .isDisplayed()).isTrue()
        );
    }

    @DisplayName("findAll - 상품 리스트를 조회할 수 있다")
    @Test
    void findAll() {
        //given
        final Product product1 = createProduct(PRODUCT_NAME, 1000L);
        final Product product2 = createProduct("상품2", 2000L);

        given(productRepository.findAll()).willReturn(Arrays.asList(product1, product2));

        final List<Product> products = productService.findAll();

        assertAll(
            () -> assertThat(products.get(0)).isEqualTo(product1),
            () -> assertThat(products.get(1)).isEqualTo(product2)
        );
    }

    private Product createProduct(final String name, final long price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    private Menu createMenu(final String name, final Long price) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setDisplayed(true);

        final Product product1 = makeProduct(PRODUCT_NAME, 10000L);
        final Product product2 = makeProduct("상품2", 20000L);

        final MenuProduct menuProduct1 = makeMenuProduct(product1, 2L);
        final MenuProduct menuProduct2 = makeMenuProduct(product2, 3L);
        final List<MenuProduct> menuProducts = Arrays.asList(menuProduct1, menuProduct2);

        menu.setMenuProducts(menuProducts);
        return menu;
    }

    private Product makeProduct(final String name, final long price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(price));
        product.setName(name);
        return product;
    }

    private MenuProduct makeMenuProduct(final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
