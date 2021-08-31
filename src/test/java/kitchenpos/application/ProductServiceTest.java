package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static kitchenpos.application.MenuServiceTest.메뉴만들기;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductServiceTest {
    private ProductService productService;
    private ProductRepository productRepository = new InMemoryProductRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private Product product;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
        product = new Product();
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(1000));
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {
        final Product expected = 상품등록(product);

        assertThat(expected).isNotNull();
        assertAll(
                () -> assertThat(expected.getId()).isNotNull(),
                () -> assertThat(expected.getName()).isEqualTo(product.getName()),
                () -> assertThat(expected.getPrice()).isEqualTo(product.getPrice())
        );
    }

    @DisplayName("상품의 가격은 빈 값이 아니어야하고, 0원 이상이어야한다.")
    @ValueSource(strings = "-1000")
    @NullSource
    @ParameterizedTest
    void create(BigDecimal price) {
        product.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 상품등록(product));
    }

    @DisplayName("상품의 이름은 빈 값이거나 비속어가 아니어야한다.")
    @ValueSource(strings = "욕설")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        product.setName(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 상품등록(product));
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        final Product saved = 상품등록(product);
        final Product request = new Product();
        request.setPrice(BigDecimal.valueOf(3_000L));

        final Product expected = 상품가격수정(saved.getId(), request);

        assertAll(
                () -> assertThat(expected.getId()).isEqualTo(saved.getId()),
                () -> assertThat(expected.getName()).isEqualTo(saved.getName()),
                () -> assertThat(expected.getPrice()).isEqualTo(BigDecimal.valueOf(3_000L))
        );
    }

    @DisplayName("상품의 가격은 0원 이상이어야한다.")
    @ValueSource(strings = "-1000")
    @NullSource
    @ParameterizedTest
    void changePrice(BigDecimal price) {
        final Product saved = 상품등록(product);
        final Product request = new Product();
        request.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->상품가격수정(saved.getId(), request));
    }

    @DisplayName("상품의 가격을 변경할 때 상품에 속한 메뉴의 가격이 메뉴 상품 가격의 총합과 다를 경우 메뉴를 노출하지 않는다.")
    @Test
    void changePrice_Menu() {
        final Product saved = 상품등록(product);
        final Menu menu = 가격변경_메뉴만들기(saved, menuRepository, BigDecimal.valueOf(7000L));
        assertThat(menuRepository.findById(menu.getId()).get().isDisplayed()).isTrue();
        final Product price = new Product();
        price.setPrice(BigDecimal.valueOf(1000L));

        상품가격수정(saved.getId(), price);

        assertThat(menuRepository.findById(menu.getId()).get().isDisplayed()).isFalse();
    }

    @DisplayName("상품을 전체 조회한다.")
    @Test
    void findAll() {
        final Product other = new Product();
        other.setName("다른 상품");
        other.setPrice(BigDecimal.valueOf(3000));
        final Product saved1 = 상품등록(product);
        final Product saved2 = 상품등록(other);

        List<Product> expected = 상품전체조회();

        assertThat(expected).containsOnly(saved1, saved2);
    }

    private Product 상품등록(final Product product) {
        return productService.create(product);
    }

    private Product 상품가격수정(final UUID productId, final Product product) {
        return productService.changePrice(productId, product);
    }

    private List<Product> 상품전체조회() {
        return productService.findAll();
    }


    public static Product 상품만들기(ProductRepository productRepository) {
        final Product product = new Product();
        product.setId(randomUUID());
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(10_000L));
        return productRepository.save(product);
    }

    private static Menu 가격변경_메뉴만들기(Product product, MenuRepository menuRepository, BigDecimal price) {
        final Menu menu = new Menu();
        menu.setId(randomUUID());
        menu.setPrice(BigDecimal.valueOf(7000L));
        menu.setDisplayed(true);
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1L);
        menu.setMenuProducts(Arrays.asList(menuProduct));
        return menuRepository.save(menu);
    }
}
