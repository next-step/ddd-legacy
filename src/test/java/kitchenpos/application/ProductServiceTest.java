package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuRepository;
import kitchenpos.application.fake.FakeProductRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductServiceTest {

    private final ProductRepository productRepository = new FakeProductRepository();
    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final PurgomalumClient purgomalumClient = new PurgomalumClient(new RestTemplateBuilder());
    private final ProductService productService = new ProductService(productRepository, menuRepository, purgomalumClient);

    @Test
    @DisplayName("상품을 생성하여 저장한다.")
    void createProduct() {
        // given
        BigDecimal price = BigDecimal.valueOf(15000);
        final Product product = createProduct("상품", price);

        // when
        Product saveProduct = productService.create(product);

        // then
        assertAll(
                () -> assertThat(saveProduct.getId()).isNotNull(),
                () -> assertThat(saveProduct.getName()).isEqualTo("상품"),
                () -> assertThat(saveProduct.getPrice()).isEqualTo(price)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -100})
    @DisplayName("상품을 가격이 음수이면 Exception을 발생 시킨다.")
    void createProductIfInvalidPriceThrowException(int priceValue) {
        // given
        BigDecimal price = BigDecimal.valueOf(priceValue);
        final Product product = createProduct("상품", price);

        // when
        // then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("상품을 가격이 존재하지 않으면 Exception을 발생 시킨다.")
    void createProductIfPriceNullThrowException(BigDecimal price) {
        // given
        final Product product = createProduct("상품", price);

        // when
        // then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"bitch"})
    @DisplayName("상품의 이름에 욕설이 포함되어 있으면 Exception을 발생 시킨다.")
    void createProductIfContainPurgomalumThrowException(String name) {
        // given
        BigDecimal price = BigDecimal.valueOf(15000);
        final Product product = createProduct(name, price);

        // when
        // then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격을 수정한다.")
    void changeProductPrice() {
        // given
        BigDecimal price = BigDecimal.valueOf(15000);
        final Product product = createProduct("상품", price);
        Product saveProduct = productService.create(product);
        BigDecimal updatePrice = BigDecimal.valueOf(20000);

        // when
        Product updateProductRequest = new Product();
        updateProductRequest.setPrice(updatePrice);
        productService.changePrice(saveProduct.getId(), updateProductRequest);
        Product updateProduct = productRepository.findById(saveProduct.getId()).get();

        // then
        assertAll(
                () -> assertThat(updateProduct.getId()).isNotNull(),
                () -> assertThat(updateProduct.getName()).isEqualTo("상품"),
                () -> assertThat(updateProduct.getPrice()).isEqualTo(updatePrice)
        );
    }

    @Test
    @DisplayName("상품 리스트를 가져온다.")
    void findProducts() {
        // given
        productService.create(createProduct("상품", BigDecimal.valueOf(15000)));
        productService.create(createProduct("상품2", BigDecimal.valueOf(20000)));

        // when
        List<Product> products = productService.findAll();

        // then
        assertThat(products.size()).isEqualTo(2);
    }

    private Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }

}
