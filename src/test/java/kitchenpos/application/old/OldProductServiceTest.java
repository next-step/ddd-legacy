package kitchenpos.application.old;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class OldProductServiceTest {

    private final String name = "순살치킨";
    private final BigDecimal price = BigDecimal.valueOf(20000L);

    private SoftAssertions softAssertions;

    @Autowired
    private ProductService productService;

    @BeforeEach
    void initSoftAssertions() {
        softAssertions = new SoftAssertions();
    }

    @AfterEach
    void checkAssertions() {
        softAssertions.assertAll();
    }

    @DisplayName("단품 생성")
    @Test
    void create() {
        Product product = productService.create(new Product(name, price));
        softAssertions.assertThat(product).isNotNull();
        softAssertions.assertThat(product.getName()).isEqualTo(name);
        softAssertions.assertThat(product.getPrice()).isEqualTo(price);
    }

    @DisplayName("단품 생성시 price validation")
    @Test
    void createValidationPrice() {

        // negative price
        softAssertions.assertThatThrownBy(() -> productService.create(new Product(name, BigDecimal.valueOf(-20000L))))
                .isInstanceOf(IllegalArgumentException.class);

        // null price
        softAssertions.assertThatThrownBy(() -> productService.create(new Product(name, null)))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("단품 생성시 name validation (null, 부적합 단어)")
    @ValueSource(strings = "shit")
    @NullSource
    @ParameterizedTest
    void createNameValidationName(String name) {

        softAssertions.assertThatThrownBy(() -> productService.create(new Product(name, price)))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("단품 가격 변경")
    @Test
    void changePrice() {
        Product product = productService.create(new Product(name, price));
        Product changeRequest = new Product(name, price.subtract(BigDecimal.valueOf(10000L)));
        Product changedProduct = productService.changePrice(product.getId(), changeRequest);
        assertThat(changedProduct.getPrice()).isEqualTo(changeRequest.getPrice());
    }

    @DisplayName("단품 가격 변경 Validation")
    @Test
    void changePriceValidation() {
        Product product = productService.create(new Product(name, price));

        // negative price
        Product negativePriceRequest = new Product(name, BigDecimal.valueOf(-20000L));
        softAssertions.assertThatThrownBy(() -> productService.changePrice(product.getId(), negativePriceRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // null price
        Product nullPriceRequest = new Product(name, null);
        softAssertions.assertThatThrownBy(() -> productService.changePrice(product.getId(), nullPriceRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // wrong target product
        Product normalRequest = new Product(name, price.subtract(BigDecimal.valueOf(10000L)));
        softAssertions.assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), normalRequest))
                .isInstanceOf(NoSuchElementException.class);

        // TODO("단품 가격 변경시 해당 단품이 속한 메뉴의 가격이 각각의 단품의 총합보다 넘는지 validation 하는 로직에 대한 검증")

    }

    @DisplayName("모든 단품 조회")
    @Test
    void findAll() {
        productService.create(new Product(name, price));
        productService.create(new Product(name, price));
        List<Product> products = productService.findAll();
        assertThat(productService.findAll().size()).isEqualTo(products.size());
    }

}
