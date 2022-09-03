package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private final static UUID DEFAULT_PRODUCT_ID = UUID.randomUUID();
    private final static String DEFAULT_PRODUCT_NAME = "후라이드 치킨";
    private final static Long DEFAULT_PRODUCT_PRICE = 10L;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    private Product createProduct(final UUID ID, final String name, final Long price) {
        Product product = new Product();

        product.setId(ID);
        product.setName(name);
        if (price != null) {
            product.setPrice(BigDecimal.valueOf(price));
        }

        return product;
    }

    private Product defaultProduct() {
        return createProduct(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);
    }

    @DisplayName("상품 생성이 가능하다")
    @Test
    void create_product() {
        final Product product = defaultProduct();

        given(purgomalumClient.containsProfanity(Mockito.anyString()))
                .willReturn(false);
        given(productRepository.save(Mockito.any(Product.class)))
                .willReturn(product);


        final Product result = productService.create(product);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(result.getName()).isEqualTo(DEFAULT_PRODUCT_NAME);
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(DEFAULT_PRODUCT_PRICE));
    }

    @DisplayName("가격은 필수 이며, 음수 일 수 없다")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = -1)
    void create_product_by_negative_number(final Long price) {
        final Product product = createProduct(DEFAULT_PRODUCT_ID, "후라이드 치킨", price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품의 이름은 필수이다")
    @Test
    void create_product_with_null_and_empty_name() {
        final Product product = createProduct(DEFAULT_PRODUCT_ID, null, 6900L);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품의 이름은 비속어가 포함될 수 없다")
    @Test
    void create_product_with_profanity() {
        final Product product = defaultProduct();

        given(purgomalumClient.containsProfanity(Mockito.anyString()))
                .willReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("생성된 상품의 가격을 변경할 수 있다")
    @Test
    void change_price() {
        final Product originProduct = defaultProduct();

        given(productRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(originProduct));

        given(menuRepository.findAllByProductId(Mockito.any(UUID.class)))
                .willReturn(new ArrayList<>());

        final Long changedPrice = 15000L;
        final Product updateProduct = createProduct(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_NAME, changedPrice);

        final Product result = productService.changePrice(DEFAULT_PRODUCT_ID, updateProduct);

        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(changedPrice));
    }

    @DisplayName("가격을 변경할 때 가격은 필수이며, 음수 일 수 없다")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = -1)
    void change_price_by_negative_number(final Long price) {
        final Product updateProduct = createProduct(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_NAME, price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(DEFAULT_PRODUCT_ID, updateProduct));
    }

    @DisplayName("생성된 상품을 조회 가능하다")
    @Test
    void select_all_products() {
        final Product defaultProduct = defaultProduct();
        final Product garlicChicken = createProduct(UUID.randomUUID(), "마늘 치킨", 10L);

        final List<Product> chickens = Arrays.asList(defaultProduct, garlicChicken);
        given(productRepository.findAll())
                .willReturn(chickens);

        final List<Product> products = productService.findAll();
        assertThat(products).isNotEmpty();
        assertThat(products.size()).isEqualTo(2);
        assertThat(products).isEqualTo(chickens);
    }
}