package kitchenpos;

import kitchenpos.application.*;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.mockito.Mockito.*;


@DisplayName(value = "ProductService 테스트")
public class ProductTest {

    private static final BigDecimal BIG_DECIMAL_MINUS_ONE = BigDecimal.valueOf(-1);
    private static final String TEST_PRODUCT_NAME = "TEST치킨";
    private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
    private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
    private static final String 후라이드치킨_MENU_NAME = "후라이드 치킨메뉴";
    public static final UUID 후라이드치킨_MENU_UUID = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
    public static final UUID 후라이드치킨_MENU_GROUP_UUID = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");
    private static final String 한마리메뉴_MENU_GROUP_NAME = "한마리메뉴";
    public static final String 비속어_NAME = "비속어";


    private ProductService productService;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;


    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakeDefaultPurgomalumClient();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName(value = "상품 등록 기능")
    @Nested
    class ProductCreateTest {

        @DisplayName(value = "가격은 0원 이상이어야 한다.")
        @Test
        void createProduct() {
            //상품명이나 가격이 없을때 에러처리
            Product product = ProductTest.createProduct(후라이드치킨_PRODUCT_UUID, "", BIG_DECIMAL_MINUS_ONE);
            ThrowingCallable throwingCallable = () -> productService.create(product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "상품명이 없거나 비속어가 있으면 안됩니다.")
        @Test
        void productInvalidName() {
            //상품명에 빈값이나 비속어가 들어간 경우
            Product product = ProductTest.createProduct(후라이드치킨_PRODUCT_UUID, 비속어_NAME, BigDecimal.ONE);
            Mockito.when(purgomalumClient.containsProfanity(Mockito.anyString())).thenReturn(true);
            //에러처리
            ThrowingCallable throwingCallable = () -> productService.create(product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "상품을 등록합니다.")
        @Test
        void productCreate() {
            //상품명에 빈값이나 비속어가 들어간 경우
            Product product = ProductTest.createProduct(후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, BigDecimal.ONE);
            //에러처리
            var responseProduct = productService.create(product);

            assertThat(productRepository.findById(responseProduct.getId())).isNotNull();
            }

    }

    @DisplayName(value = "상품 가격 변경 기능")
    @Nested
    class ProductPriceChangeTest {


        @DisplayName(value = "상품의 가격을 변경합니다")
        @Test
        void changeProductPrice() {

            Product product = createProduct();
            productRepository.save(product);
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu(createMenuGroup(), menuProduct);
            menuRepository.save(menu);

            product.setPrice(BigDecimal.valueOf(21000));
            productService.changePrice(product.getId(), product);

            assertThat(menuRepository.findById(후라이드치킨_MENU_UUID).orElseThrow().isDisplayed()).isTrue();

        }

        @DisplayName(value = "변경할 상품의 가격은 0원 이상이어야 한다.")
        @Test
        void zeroProductPrice() {
            //상품명이나 가격이 없을때 에러처리
            Product product = createProduct(BIG_DECIMAL_MINUS_ONE);
            ThrowingCallable throwingCallable = () -> productService.changePrice(ProductTest.후라이드치킨_PRODUCT_UUID, product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴의 가격이 메뉴의 상품들의 총 가격 합보다 크면, 비노출처리합니다")
        @Test
        void invalidTotalProductPrice() {
            Product product = createProduct();
            productRepository.save(product);
            Menu menu = createMenu(createMenuGroup(), createMenuProduct(product, 1));
            menuRepository.save(menu);
            product.setPrice(BigDecimal.valueOf(19000));

            productService.changePrice(product.getId(), product);
            assertThat(menuRepository.findById(후라이드치킨_MENU_UUID).orElseThrow().isDisplayed()).isFalse();
        }
    }

    @DisplayName(value = "모든 상품 조회 기능")
    @Nested
    class AllProductFindTest {
        @DisplayName(value = "모든 상품을 조회합니다")
        @Test
        void changeProductPrice() {
            Product product = createProduct();
            productRepository.save(product);
            List<Product> products = productService.findAll();
            assertThat(products.size()).isEqualTo(1);
        }
    }

    private Product createProduct() {
        Product product = new Product();
        product.setId(후라이드치킨_PRODUCT_UUID);
        product.setName(TEST_PRODUCT_NAME);
        product.setPrice(후라이드치킨_DEFAULT_PRICE);
        return product;
    }

    private MenuProduct createMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private Product createProduct(BigDecimal bigDecimalMinusOne) {
        Product product = new Product();
        product.setId(후라이드치킨_PRODUCT_UUID);
        product.setName(TEST_PRODUCT_NAME);
        product.setPrice(BIG_DECIMAL_MINUS_ONE);
        return product;
    }


    private static Product createProduct(UUID uuid, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(uuid);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private static MenuGroup createMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(한마리메뉴_MENU_GROUP_NAME);
        menuGroup.setId(후라이드치킨_MENU_GROUP_UUID);
        return menuGroup;
    }

    private static MenuGroup createMenuGroup(String name, UUID id) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(id);
        return menuGroup;
    }

    private static Menu createMenu(MenuGroup menuGroup, MenuProduct menuProducts) {
        Menu menu = new Menu();
        menu.setId(후라이드치킨_MENU_UUID);
        menu.setName(후라이드치킨_MENU_NAME);
        menu.setPrice(후라이드치킨_DEFAULT_PRICE);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(menuProducts));
        menu.setMenuGroupId(후라이드치킨_MENU_GROUP_UUID);
        return menu;
    }

    private static Menu createMenu(UUID id, String name, BigDecimal price, MenuGroup menuGroup, List<MenuProduct> menuProducts, UUID menuGroupId) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
