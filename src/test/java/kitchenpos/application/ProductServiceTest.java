package kitchenpos.application;

import static kitchenpos.application.MenuProductFixture.뿌링클_1개;
import static kitchenpos.application.MenuProductFixture.콜라_1개;
import static kitchenpos.application.ProductFixture.뿌링클;
import static kitchenpos.application.ProductFixture.콜라;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.ProfanityClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("상품 관리")
class ProductServiceTest {

    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final ProfanityClient profanityClient = new FakeProfanityClient();

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, profanityClient);
    }

    @DisplayName("가격은 0원 이상이어야 한다.")
    @ParameterizedTest(name = "상품금액: [{arguments}]")
    @ValueSource(strings = {"-1"})
    @NullSource
    void createPriceException(BigDecimal price) {
        //given
        Product product = 상품_생성(price);

        //when
        ThrowingCallable actual = () -> productService.create(product);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이름에 비속어를 사용할 수 없다.")
    @ParameterizedTest(name = "상품 이름: [{arguments}]")
    @ValueSource(strings = {"비속어", "욕"})
    @NullSource
    void createNameException(String name) {
        //given
        Product product = 상품_생성(name);

        //when
        ThrowingCallable actual = () -> productService.create(product);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격을 변경할 수 있다. 가격이 해당 상품을 포함하는 메뉴의 가격보다 크면 메뉴를 진열하지 않는다.")
    @ParameterizedTest(name = "변경할 가격: [{0}], 진열 여부: [{1}]")
    @CsvSource(value = {
        "8000, false",
        "15000, true"
    })
    void changePrice(long price, boolean expectedDisplayed) {
        //given
        Product 신규_상품 = productRepository.save(상품_생성(12_000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(신규_상품);
        menuProduct.setProductId(신규_상품.getId());
        menuProduct.setQuantity(1);

        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(menuProduct, 콜라_1개));
        menu.setPrice(BigDecimal.valueOf(11_000L));
        menuRepository.save(menu);

        Product 변경할_금액 = 상품_생성(price);

        //when
        Product product = productService.changePrice(신규_상품.getId(), 변경할_금액);

        //then
        assertAll(
            () -> assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(price)),
            () -> assertThat(menu.isDisplayed()).isEqualTo(expectedDisplayed)
        );
    }

    @DisplayName("0원 미만의 가격으로 변경할 수 없다.")
    @ParameterizedTest(name = "변경할 가격: [{arguments}]")
    @ValueSource(strings = {"-1"})
    @NullSource
    void changePriceException(BigDecimal price) {
        //given
        Product 사이다 = 상품_생성(2_000L);
        productRepository.save(사이다);

        Product 사이다_가격_변경 = 상품_생성(price);

        //when
        ThrowingCallable actual = () -> productService.changePrice(사이다.getId(), 사이다_가격_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("등록되지 않은 상품은 가격을 변경할 수 없다.")
    @Test
    void changePriceNotExistProductException() {
        //given
        UUID 등록되지_않은_상품_ID = UUID.randomUUID();

        Product 뿌링클_가격_변경 = 상품_생성(10_000L);

        //when
        ThrowingCallable actual = () -> productService.changePrice(등록되지_않은_상품_ID, 뿌링클_가격_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("등록된 상품들을 조회할 수 있다.")
    @Test
    void findAll() {
        //given
        productRepository.save(뿌링클);
        productRepository.save(콜라);

        //when
        List<Product> products = productService.findAll();

        //then
        assertThat(products).hasSize(2);

    }

    private Product 상품_생성(String name) {
        return 상품_생성(name, BigDecimal.valueOf(1_000L));
    }

    private Product 상품_생성(long price) {
        return 상품_생성(BigDecimal.valueOf(price));
    }

    private Product 상품_생성(BigDecimal price) {
        return 상품_생성("상품", price);
    }

    private Product 상품_생성(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
