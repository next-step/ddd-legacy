package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProductServiceTest {

    private final String name = "순살치킨";
    private final BigDecimal price = BigDecimal.valueOf(20000L);
    private SoftAssertions softAssertions;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

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
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        softAssertions.assertAll();
    }

    @DisplayName("단품 생성 Validation")
    @Test
    void createValidation() {

        // negative price
        assertThatThrownBy(() -> productService.create(new Product(name, BigDecimal.valueOf(-20000L))))
                .isInstanceOf(IllegalArgumentException.class);

        // null price
        assertThatThrownBy(() -> productService.create(new Product(name, null)))
                .isInstanceOf(IllegalArgumentException.class);

        // contains profanity name
        assertThatThrownBy(() -> productService.create(new Product("shit", price)))
                .isInstanceOf(IllegalArgumentException.class);

        // null name
        assertThatThrownBy(() -> productService.create(new Product(null, price)))
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
        Product normalRequest = new Product(name, price.subtract(BigDecimal.valueOf(10000L)));
        Product negativePriceRequest = new Product(name, BigDecimal.valueOf(-20000L));
        Product nullPriceRequest = new Product(name, null);

        // negative price
        assertThatThrownBy(() -> productService.changePrice(product.getId(), negativePriceRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // null price
        assertThatThrownBy(() -> productService.changePrice(product.getId(), nullPriceRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // wrong target product
        assertThatThrownBy(() -> productService.changePrice(getNewUUID(), normalRequest))
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

    private UUID getNewUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (isExistsUUID(uuid));

        return uuid;
    }

    private boolean isExistsUUID(UUID uuid) {
        if (productRepository.existsById(uuid)) {
            return true;
        }

        return false;
    }
}

