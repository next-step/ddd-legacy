package kitchenpos.application;

import kitchenpos.domain.*;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductServiceTest {
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private ProductService productService;

    private String name;
    private BigDecimal price;
    private Product product;
    private UUID productId;
    private Menu menu;

    @BeforeEach
    void setUp() {
        productRepository = new TestProductRepository();
        menuRepository = new TestMenuRepository();
        productService = new ProductService(
                productRepository,
                menuRepository,
                new TestPurgomalumClient()
        );

        name = "상품";
        price = BigDecimal.valueOf(500L);
        product = saveProduct();
        productId = product.getId();
        menu = menuRepository.save(createMenu(getMenuProduct(product)));
    }

    @Test
    @DisplayName("상품을 생성한다.")
    void create() {
        Product product = saveProduct();

        assertAll(
                () -> assertEquals(product.getName(), name),
                () -> assertEquals(product.getPrice(), price)
        );
    }

    @NullSource
    @ValueSource(strings = "-1")
    @ParameterizedTest
    @DisplayName("상품 가격은 0 이상이어야 한다.")
    void create_valid_price(BigDecimal price) {
        Product request = createRequest(name, price);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @NullSource
    @ValueSource(strings = {"하드코딩", "레거시"})
    @ParameterizedTest
    @DisplayName("상품명에 비속어가 포함되면 안된다.")
    void create_valid_name(String name) {
        Product request = createRequest(name, price);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 가격을 변경한다.")
    void changePrice() {
        BigDecimal price = BigDecimal.valueOf(100L);
        Product request = createChangePriceRequest(price);

        Product product = productService.changePrice(productId, request);

        assertAll(
                () -> assertEquals(product.getId(), productId),
                () -> assertEquals(product.getPrice(), price)
        );
    }

    @NullSource
    @ValueSource(strings = "-1")
    @ParameterizedTest
    @DisplayName("상품 가격 변경시 가격은 0 이상이어야 한다.")
    void changePrice_valid_price(BigDecimal price) {
        Product request = createChangePriceRequest(price);

        assertThatThrownBy(() -> productService.changePrice(productId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @CsvSource(value = {"500, true", "499, false"})
    @ParameterizedTest
    @DisplayName("변경 가격에 따라 메뉴 노출 상태 변경")
    void changePrice_menu_display_change(BigDecimal price, boolean isDisplayed) {
        Product request = createChangePriceRequest(price);

        productService.changePrice(product.getId(), request);

        assertThat(menu.isDisplayed()).isEqualTo(isDisplayed);
    }

    @Test
    @DisplayName("상품을 전체 조회한다.")
    void findAll() {
        List<Product> products = productService.findAll();

        AssertionsForInterfaceTypes.assertThat(products).hasSize(1);
    }

    private Product saveProduct() {
        Product request = createRequest(name, price);
        return productService.create(request);
    }

    private List<MenuProduct> getMenuProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(2L);
        return Collections.singletonList(menuProduct);
    }

    private Menu createMenu(List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName("메뉴");
        menu.setPrice(BigDecimal.valueOf(1000L));
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menu;
    }

    private Product createRequest(String name, BigDecimal price) {
        Product request = new Product();
        request.setName(name);
        request.setPrice(price);
        return request;
    }

    private Product createChangePriceRequest(BigDecimal price) {
        Product request = new Product();
        request.setPrice(price);
        return request;
    }
}
