package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private PurgomalumClient purgomalumClient;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        purgomalumClient = new FakePurgomalumClient(false);
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create_with_valid_attribute() {
        final String givenProductName = "test";
        final UUID givenUUID = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801");
        final BigDecimal givenPrice = BigDecimal.valueOf(1000);
        final Product request = createProduct(givenProductName, givenUUID, givenPrice);

        final Product actual = productService.create(request);

        assertThat(actual).isNotNull();
    }

    @DisplayName("상품은 가격이 존재해야한다.")
    @Test
    void create_with_null_price() {
        final String givenProductName = "test";
        final UUID givenUUID = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801");
        final BigDecimal givenPrice = null;
        final Product request = createProduct(givenProductName, givenUUID, givenPrice);

        assertThatCode(
                () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품은 가격이 0 보다 커야한다")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1000, -5000, -10000})
    void create_with_negative_price(int price) {
        final String givenProductName = "test";
        final UUID givenUUID = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801");
        final BigDecimal givenPrice = BigDecimal.valueOf(price);
        final Product request = createProduct(givenProductName, givenUUID, givenPrice);

        assertThatCode(
                () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 이름이 존재해야 한다.")
    @ParameterizedTest
    @NullSource
    void create_with_empty_name(String name) {
        final UUID givenUUID = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801");
        final BigDecimal givenPrice = BigDecimal.valueOf(1000);
        final Product request = createProduct(name, givenUUID, givenPrice);

        assertThatCode(
                () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품이름에 비속어가 들어가면 안된다.")
    @Test
    void create_with_not_allowed_name() {
        final String givenProductName = "대충 심한욕";
        final UUID givenUUID = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801");
        final BigDecimal givenPrice = BigDecimal.valueOf(1000);
        final Product request = createProduct(givenProductName, givenUUID, givenPrice);
        productService = new ProductService(productRepository, menuRepository, new FakePurgomalumClient(true));

        assertThatCode(
                () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 목록을 조회할 수 있다")
    @Test
    void get_products() {
        final UUID givenUUID1 = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801");
        final UUID givenUUID2 = UUID.fromString("b619cf4e-3725-48b3-9e32-84eb2e92e5b9");
        final String givenName1 = "test1";
        final String givenName2 = "test2";
        final BigDecimal givenPrice1 = BigDecimal.valueOf(1000);
        final BigDecimal givenPrice2 = BigDecimal.valueOf(1000);
        final Product request1 = createProduct(givenName1, givenUUID1, givenPrice1);
        final Product request2 = createProduct(givenName2, givenUUID2, givenPrice2);
        final Product product1 = productRepository.save(request1);
        final Product product2 = productRepository.save(request2);

        List<Product> actual = productService.findAll();

        assertThat(actual).containsAll(Arrays.asList(product1, product2));
    }

    @DisplayName("상품의 가격을 변경 할 수 있다.")
    @Test
    void change_product_with_valid_price() {
        final Product request = createProduct(
                "test1",
                UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801"),
                BigDecimal.valueOf(1000)
        );
        Product givenProduct = productService.create(request);
        final BigDecimal changePrice = BigDecimal.valueOf(2000);
        final Product changeRequest = createProduct(null, null, changePrice);

        final Product actual = productService.changePrice(givenProduct.getId(), changeRequest);

        assertThat(actual.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("상품은 가격이 존재해야한다.")
    @Test
    void change_product_price_with_null_price() {
        final Product request = createProduct(
                "test1",
                UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801"),
                BigDecimal.valueOf(1000)
        );
        Product givenProduct = productService.create(request);
        final BigDecimal changePrice = null;
        final Product changeRequest = createProduct(null, null, changePrice);

        assertThatCode(() ->
                productService.changePrice(givenProduct.getId(), changeRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격 변경시 상품은 가격이 0 보다 커야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1000, -5000})
    void change_product_price_with_negative_price(int price) {
        final Product request = createProduct(
                "test1",
                UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801"),
                BigDecimal.valueOf(1000)
        );
        Product givenProduct = productService.create(request);
        final BigDecimal changePrice = BigDecimal.valueOf(price);
        final Product changeRequest = createProduct(null, null, changePrice);

        assertThatCode(() ->
                productService.changePrice(givenProduct.getId(), changeRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격 변경시 상품정보가 존재해야한다")
    @Test
    void change_product_price_with_not_exist_product() {
        final String not_exist_uuid = "06fe3514-a8a6-48ed-85e6-e7296d0e1800";
        final Product not_exist_product = productRepository.getById(UUID.fromString(not_exist_uuid));
        final BigDecimal changePrice = BigDecimal.valueOf(1000);
        final Product changeRequest = createProduct(null, null, changePrice);

        assertThatCode(() ->
                productService.changePrice(not_exist_product.getId(), changeRequest)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴가격이 메뉴 상품의 합보다 크다면 메뉴 전시상태를 비활성화한다.")
    @Test
    void menu_display_false_when_change_price_bigger_than_menu_price() {
        final Product request = createProduct(
                "test1",
                UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1801"),
                BigDecimal.valueOf(1000)
        );
        final Product saved = productService.create(request);
        final Menu savedMenu = saveMenu(saved);
        final BigDecimal changePrice = BigDecimal.valueOf(5000);
        final Product changeRequest = createProduct(null, null, changePrice);

        productService.changePrice(saved.getId(), changeRequest);
        final Menu menu = menuRepository.findById(savedMenu.getId())
                .orElseThrow(EntityNotFoundException::new);

        assertThat(menu.isDisplayed()).isFalse();
    }

    private Menu saveMenu(Product saved) {
        MenuProduct menuProduct = createMenuProduct(saved);
        Menu menu = createMenu(2000, "test menu1", true, Collections.singletonList(menuProduct));
        return menuRepository.save(menu);
    }

    private MenuProduct createMenuProduct(Product saved) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(saved);
        menuProduct.setProductId(saved.getId());
        return menuProduct;
    }

    private Menu createMenu(int price, String name, boolean display, List<MenuProduct> products) {
        MenuGroup menuGroup = menuGroupRepository.findAll()
                .stream().
                findAny()
                .orElseThrow(EntityNotFoundException::new);
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(display);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(products);

        return menu;
    }

    private Product createProduct(String givenProductName, UUID givenUUID, BigDecimal givenPrice) {
        Product product = new Product();
        product.setId(givenUUID);
        product.setName(givenProductName);
        product.setPrice(givenPrice);
        return product;
    }
}