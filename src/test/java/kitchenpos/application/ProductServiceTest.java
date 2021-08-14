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

class ProductServiceTest {
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private ProductService productService;

    private String name;
    private BigDecimal price;

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
    }

    @Test
    @DisplayName("상품을 생성한다.")
    void create() {
        Product product = saveProduct();

        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
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
        UUID productId = saveProduct().getId();
        BigDecimal price = BigDecimal.valueOf(100L);
        Product request = createChangePriceRequest(price);

        Product product = productService.changePrice(productId, request);

        assertThat(product.getId()).isEqualTo(productId);
        assertThat(product.getPrice()).isEqualTo(price);
    }

    @NullSource
    @ValueSource(strings = "-1")
    @ParameterizedTest
    @DisplayName("상품 가격 변경시 가격은 0 이상이어야 한다.")
    void changePrice_valid_price(BigDecimal price) {
        UUID productId = saveProduct().getId();
        Product request = createChangePriceRequest(price);

        assertThatThrownBy(() -> productService.changePrice(productId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @CsvSource(value = {"500, true", "499, false"})
    @ParameterizedTest
    @DisplayName("변경 가격에 따라 메뉴 노출 상태 변경")
    void changePrice_menu_display_change(BigDecimal price, boolean isDisplayed) {
        Product product = saveProduct();
        Menu menu = saveMenu(getMenuProduct(product));
        Product request = createChangePriceRequest(price);

        productService.changePrice(product.getId(), request);

        assertThat(menu.isDisplayed()).isEqualTo(isDisplayed);
    }

    private List<MenuProduct> getMenuProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(2L);
        return Collections.singletonList(menuProduct);
    }

    private Menu saveMenu(List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName("메뉴");
        menu.setPrice(BigDecimal.valueOf(1000L));
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menuRepository.save(menu);
    }

    @Test
    @DisplayName("상품을 전체 조회한다.")
    void findAll() {
        saveProduct();
        saveProduct();

        List<Product> products = productService.findAll();

        AssertionsForInterfaceTypes.assertThat(products).hasSize(2);
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

    private Product saveProduct() {
        Product request = createRequest(name, price);
        return productService.create(request);
    }
}