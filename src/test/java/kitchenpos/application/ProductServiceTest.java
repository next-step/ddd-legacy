package kitchenpos.application;

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
import kitchenpos.domain.Product;
import kitchenpos.fake.FakePugomalumClinet;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.web.client.RestTemplateBuilder;

class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(new InMemoryProductRepository()
                , new InMemoryMenuRepository()
                , new FakePugomalumClinet(new RestTemplateBuilder()));
    }


    @DisplayName("상품을 등록할때 상품의 가격은 필수이며 0원 이상이어야 한다.")
    @MethodSource("bigDecimalZeroAndNull")
    @ParameterizedTest
    void product_price_is_not_null_and_less_then_zero(BigDecimal price){
         Product product = createProduct(price);

        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(product)
        );
    }

    @DisplayName("상품을 등록할때 상품의 이름은 필수 여야 한다.")
    @Test
    void product_name_is_not(){
        Product product = createProduct((String) null);

        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(product)
        );
    }

    @DisplayName("상품을 등록할때 상품의 이름은 욕설이 포함되면 안된다.")
    @Test
    void product_name_notIn_purgomalum(){
        Product product = createProduct("욕설");

        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(product)
        );
    }

    @DisplayName("변경할 상품의 가격이 0원 이상이어야 변경이 가능하다.")
    @ParameterizedTest
    @MethodSource("bigDecimalZeroAndNull")
    void change_price_enable_product(BigDecimal price) {
        Product request = createProduct(price);

        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.changePrice(UUID.randomUUID(), request)
        );
    }

    @Test
    @DisplayName("변경할 상품이 등록 되어 있어야 변경이 가능하다.")
    void change_price_registed_product() {
        Product product = createProduct();

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), product));
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
