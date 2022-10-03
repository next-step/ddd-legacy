package kitchenpos.application;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.ProductFixture.changeProduct;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.fake.FakePugomalumClinet;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("상품 테스트")
class ProductServiceTest {

    private ProductService productService;
    private MenuRepository menuRepository;


    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        productService = new ProductService(new InMemoryProductRepository()
                , menuRepository
                , new FakePugomalumClinet());
    }


    @DisplayName("상품을 등록할때 상품의 가격은 필수이며 0원 이상이어야 한다.")
    @MethodSource("bigDecimalZeroAndNull")
    @ParameterizedTest
    void product_price_is_not_null_and_less_then_zero(BigDecimal price) {
        Product product = createProduct(price);

        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(product)
        );
    }

    @DisplayName("상품을 등록할때 상품의 이름은 필수 여야 한다.")
    @Test
    void product_name_is_not() {
        Product product = createProduct((String) null);

        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(product)
        );
    }

    @DisplayName("상품을 등록할때 상품의 이름은 욕설이 포함되면 안된다.")
    @Test
    void product_name_notIn_purgomalum() {
        Product product = createProduct("욕설");

        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(product)
        );
    }

    @DisplayName("변경할 상품의 가격이 0원 이상이어야 가격 변경이 가능하다.")
    @ParameterizedTest
    @MethodSource("bigDecimalZeroAndNull")
    void change_price_enable_product(BigDecimal price) {
        Product request = createProduct(price);

        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.changePrice(UUID.randomUUID(), request)
        );
    }

    @Test
    @DisplayName("변경할 상품이 등록 되어 있어야 가격 변경이 가능하다.")
    void change_price_registed_product() {
        Product product = createProduct();

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
    }

    @Test
    @DisplayName("상품의 가격이 변경된다.")
    void changePrice() {
        // given
        Product product = productService.create(createProduct(BigDecimal.valueOf(1_000)));
        Menu menu = createMenu(BigDecimal.valueOf(1_000));
        menu.setMenuProducts(List.of(createMenuProduct(product, 2)));
        menuRepository.save(menu);
        Product request = changeProduct(product, BigDecimal.valueOf(500));

        //when
        final Product changedProduct = productService.changePrice(product.getId(), request);

        //then
        assertThat(changedProduct.getPrice()).isEqualTo(request.getPrice());
    }

    @Test
    @DisplayName("포함된 메뉴의 가격의 합계가 메뉴의 가격 보다 클 경우 메뉴는 숨겨진다")
    void changePrice_display() {
        // given
        Product product = productService.create(createProduct(BigDecimal.valueOf(1_000)));
        Menu menu = createMenu(BigDecimal.valueOf(1_000));
        menu.setMenuProducts(List.of(createMenuProduct(product, 2)));
        final Menu savedMenu = menuRepository.save(menu);
        Product request = changeProduct(product, BigDecimal.valueOf(1000));

        //when
        final Product changedProduct = productService.changePrice(product.getId(), request);

        //then
        Menu findMenu = menuRepository.findById(savedMenu.getId()).get();
        assertAll(
                () -> assertThat(findMenu.isDisplayed()).isFalse(),
                () -> assertThat(changedProduct.getPrice()).isEqualTo(request.getPrice())
        );
    }

    @Test
    @DisplayName("등록된 상품들을 찾는다.")
    void findAll() {
        final Product product1 = registedProduct(createProduct("상품1"));
        final Product product2 = registedProduct(createProduct("상품2"));

        final List<Product> findProducts = productService.findAll();

        assertAll(
                () -> assertThat(findProducts).extracting("id").contains(product1.getId(), product2.getId()),
                () -> assertThat(findProducts).extracting("name").contains(product1.getName(), product2.getName())
        );
    }


    private Product registedProduct(Product product) {
        return productService.create(product);
    }


    private static Stream<BigDecimal> bigDecimalZeroAndNull() {
        return Stream.of(null, BigDecimal.valueOf(-1));
    }

}
