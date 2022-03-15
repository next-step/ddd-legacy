package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @MockBean
    private PurgomalumClient purgomalumClient;

    @DisplayName("상품을 등록한다.")
    @Test
    void create() {
        // given
        Product productRequest = createProductRequest("후라이드치킨", new BigDecimal("15000"));

        // when
        Product actual = productService.create(productRequest);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo("후라이드치킨"),
                () -> assertThat(actual.getPrice()).isEqualByComparingTo(new BigDecimal("15000"))
        );
    }

    @DisplayName("상품에는 반드시 이름이 있어야 한다.")
    @NullSource
    @ParameterizedTest
    void nameIsMandatory(String name) {
        // given when
        Product productRequest = createProductRequest(name, new BigDecimal("15000"));

        // then
        assertThatThrownBy(() -> productService.create(productRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름에는 비속어를 사용할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"xxxx"})
    void nameContainsProfanity(String name) {
        // given
        Product productRequest = createProductRequest(name, new BigDecimal("15000"));

        // when
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> productService.create(productRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품에는 반드시 가격이 있어야 한다.")
    @Test
    void priceIsMandatory() {
        // given when
        Product productRequest = createProductRequest("후라이드치킨", null);

        // then
        assertThatThrownBy(() -> productService.create(productRequest))
        .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격은 0보다 작을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-1000"})
    void negativePrice(String price) {
        // given when
        Product productRequest = createProductRequest("후라이드치킨", new BigDecimal(price));

        assertThatThrownBy(() -> productService.create(productRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        // given
        Product productRequest = createProductRequest("후라이드치킨", BigDecimal.ZERO);
        Product product = productService.create(productRequest);

        // when
        Product priceChangeRequest = new Product();
        priceChangeRequest.setPrice(new BigDecimal("1000"));
        productService.changePrice(product.getId(), priceChangeRequest);

        // then
        Optional<Product> changedProduct = productRepository.findById(product.getId());
        assertThat(changedProduct).isPresent();
        assertThat(changedProduct.get().getPrice()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @DisplayName("등록한 상품 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        Product product1Request = createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product1 = productService.create(product1Request);
        Product product2Request = createProductRequest("양념치킨", new BigDecimal("16000"));
        Product product2 = productService.create(product2Request);

        // when
        List<Product> products = productService.findAll();

        // then
        assertThat(products.stream().map(Product::getId).toArray()).contains(new UUID[] {product1.getId(), product2.getId()});
    }

    public static Product createProductRequest(String name, BigDecimal price) {
        Product productRequest = new Product();
        productRequest.setName(name);
        productRequest.setPrice(price);
        return productRequest;
    }
}
