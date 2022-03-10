package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.util.MenuFactory;
import kitchenpos.util.MenuGroupFactory;
import kitchenpos.util.ProductFactory;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        MenuGroup request = MenuGroupFactory.createMenuGroup(UUID.randomUUID(), "test group");
        menuGroupRepository.save(request);
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create_with_valid_attribute() {
        final String givenProductName = "test";
        final BigDecimal givenPrice = BigDecimal.valueOf(1000);
        final Product request = ProductFactory.createProduct(null, givenProductName, givenPrice);

        final Product actual = productService.create(request);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(givenProductName),
                () -> assertThat(actual.getPrice()).isEqualTo(givenPrice)
        );
    }

    @DisplayName("상품은 가격이 존재해야한다.")
    @Test
    void create_with_null_price() {
        final String givenProductName = "test";
        final BigDecimal givenPrice = null;
        Product request = ProductFactory.createProduct(null, givenProductName, givenPrice);

        assertThatCode(
                () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품은 가격이 0 보다 커야한다")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1000, -5000, -10000})
    void create_with_negative_price(int price) {
        final String givenProductName = "test";
        final BigDecimal givenPrice = BigDecimal.valueOf(price);
        final Product request = ProductFactory.createProduct(null, givenProductName, givenPrice);

        assertThatCode(
                () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 이름이 존재해야 한다.")
    @ParameterizedTest
    @NullSource
    void create_with_empty_name(String name) {
        final String givenProductName = name;
        final BigDecimal givenPrice = BigDecimal.valueOf(1000);
        final Product request = ProductFactory.createProduct(null, givenProductName, givenPrice);
        assertThatCode(
                () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품이름에 비속어가 들어가면 안된다.")
    @Test
    void create_with_not_allowed_name() {
        final String givenProductName = "대충 심한욕";
        final BigDecimal givenPrice = BigDecimal.valueOf(1000);
        final Product request = ProductFactory.createProduct(null, givenProductName, givenPrice);
        final boolean containsNotAllowedWords = true;
        productService = new ProductService(productRepository, menuRepository, new FakePurgomalumClient(containsNotAllowedWords));

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
        final Product request1 = ProductFactory.createProduct(givenUUID1, givenName1, givenPrice1);
        final Product request2 = ProductFactory.createProduct(givenUUID2, givenName2, givenPrice2);
        final Product product1 = productRepository.save(request1);
        final Product product2 = productRepository.save(request2);

        List<Product> actual = productService.findAll();

        assertThat(actual).containsAll(Arrays.asList(product1, product2));
    }

    @DisplayName("상품의 가격을 변경 할 수 있다.")
    @Test
    void change_product_with_valid_price() {
        final BigDecimal givenPrice = BigDecimal.valueOf(1000);
        final Product request = ProductFactory.createProduct(null, "test product", givenPrice);
        final Product givenProduct = productService.create(request);
        final BigDecimal changePrice = BigDecimal.valueOf(2000);
        final Product changeRequest = ProductFactory.createProduct(null, null, changePrice);

        final Product actual = productService.changePrice(givenProduct.getId(), changeRequest);

        assertThat(actual.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("상품은 가격이 존재해야한다.")
    @Test
    void change_product_price_with_null_price() {
        final Product request = ProductFactory.createProduct(null, "test1", BigDecimal.valueOf(1000));
        final Product givenProduct = productService.create(request);
        final BigDecimal changePrice = null;
        final Product changeRequest = ProductFactory.createProduct(null, null, changePrice);

        assertThatCode(() ->
                productService.changePrice(givenProduct.getId(), changeRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격 변경시 상품은 가격이 0 보다 커야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1000, -5000})
    void change_product_price_with_negative_price(int changePrice) {
        final Product request = ProductFactory.createProduct(null, "test1", BigDecimal.valueOf(1000));
        final Product givenProduct = productService.create(request);
        final Product changeRequest = ProductFactory.createProduct(null, null, BigDecimal.valueOf(changePrice));

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
        final Product changeRequest = ProductFactory.createProduct(null, null, changePrice);

        assertThatCode(() ->
                productService.changePrice(not_exist_product.getId(), changeRequest)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴가격이 메뉴 상품의 합보다 크다면 메뉴 전시상태를 비활성화한다.")
    @Test
    void menu_display_false_when_change_price_bigger_than_menu_price() {
        final String givenProductName = "test";
        final BigDecimal givenPrice = BigDecimal.valueOf(1000);
        final Product request = ProductFactory.createProduct(null, givenProductName, givenPrice);
        final Product saved = productService.create(request);
        final Menu savedMenu = saveMenu(saved, BigDecimal.valueOf(7000), "test menu1");
        final BigDecimal changePrice = BigDecimal.valueOf(5000);
        final Product changeRequest = ProductFactory.createProduct(null, null, changePrice);

        productService.changePrice(saved.getId(), changeRequest);

        final Menu menu = menuRepository.findById(savedMenu.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertThat(menu.isDisplayed()).isFalse();
    }

    private Menu saveMenu(Product saved, BigDecimal menuPrice, String menuName) {
        MenuProduct menuProduct = MenuFactory.createMenuProductWithQuantity(saved, 1);
        Menu menu = MenuFactory.createMenu(UUID.randomUUID(), menuPrice, menuName, true, findMenuGroup(), Collections.singletonList(menuProduct));
        return menuRepository.save(menu);
    }

    private MenuGroup findMenuGroup() {
        return menuGroupRepository.findAll()
                .stream()
                .findAny()
                .orElseThrow(EntityNotFoundException::new);
    }
}
