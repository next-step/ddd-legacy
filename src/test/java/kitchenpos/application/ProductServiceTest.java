package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuRepository;
import kitchenpos.application.fake.FakeProductRepository;
import kitchenpos.application.fake.FakeProfanityClient;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.ProfanityClient;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest {

    private final ProductRepository productRepository = new FakeProductRepository();
    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final ProfanityClient profanityClient = new FakeProfanityClient();

    private final ProductService service = new ProductService(productRepository, menuRepository, profanityClient);

    ProductServiceTest() {
    }


    @Test
    @DisplayName("상품은 이름과 가격을 입력하여 등록할 수 있다.")
    void create() {
        Product product = getProduct("상품 이름", 10000L);

        Product savedProduct = service.create(product);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo(product.getName());
        assertThat(savedProduct.getPrice()).isEqualTo(product.getPrice());
    }

    @ParameterizedTest
    @DisplayName("상품을 등록 할 때 가격은 비어있거나 0이하의 수를 입력할 수 없다.")
    @MethodSource("priceMethodSource")
    void create_price_not_empty_or_zero(Long price) {
        Product product = getProduct("상품 이름", price);

        assertThatThrownBy(() -> service.create(product))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @ParameterizedTest
    @DisplayName("상품을 등록 할 때 이름은 비어있거나 욕설이 포함될 수 없다.")
    @MethodSource("nameMethodSource")
    void create_name_not_empty(String name) {
        Product product = getProduct(name, 1000L);

        assertThatThrownBy(() -> service.create(product))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("등록한 상품의 가격을 수정할 수 있다.")
    void change_price() {
        Product product = getProduct("상품 이름", 1000L);
        Product savedProduct = service.create(product);

        BigDecimal changedPrice = BigDecimal.valueOf(2000L);
        savedProduct.setPrice(changedPrice);

        Product updateProduct = service.changePrice(savedProduct.getId(), savedProduct);

        assertThat(updateProduct.getPrice()).isEqualTo(savedProduct.getPrice());
    }

    @Test
    @DisplayName("상품의 가격을 수정 할 때 가격은 비어있거나 0이하의 수를 입력할 수 없다.")
    void change_price_not_empty_or_zero() {

        Product product = getProduct("상품 이름", 1000L);
        Product savedProduct = service.create(product);

        BigDecimal changedPrice = BigDecimal.valueOf(-1L);
        savedProduct.setPrice(changedPrice);

        assertThatThrownBy(() -> service.changePrice(savedProduct.getId(), savedProduct))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("없는 상품의 가격을 수정하는 경우 실패한다.")
    void changePrice_no_such_product() {

        Product product = getProduct("상품 이름", 1000L);

        assertThatThrownBy(() -> service.changePrice(UUID.randomUUID(), product))
                .isInstanceOf(NoSuchElementException.class);
    }



    @Test
    @DisplayName("등록한 모든 상품을 조회할 수 있다.")
    void findAll() {

        Product product1 = service.create(getProduct("상품 이름", 1000L));
        Product product2 = service.create(getProduct("상품 이름", 1000L));
        Product product3 = service.create(getProduct("상품 이름", 1000L));
        List<Product> savedProducts = Lists.list(product1, product2, product3);

        List<Product> products = service.findAll();

        assertThat(products).containsAll(savedProducts);
    }


    private static Product getProduct(String name, Long price) {
        return getProduct(name, getPrice(price));
    }
    
    private static Product getProduct(String name, BigDecimal price) {
        return new Product(name, price);
    }

    private static BigDecimal getPrice(Long price) {
        if(price == null) {
            return null;
        }

        return BigDecimal.valueOf(price);
    }

    static Stream<Long> priceMethodSource() {
        return Stream.of(-1L, null);
    }

    static Stream<String> nameMethodSource() {
        return Stream.of("비속어", null);
    }
}