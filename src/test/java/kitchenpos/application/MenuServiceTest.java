package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuGroupService menuGroupService;
    @Autowired
    private ProductService productService;

    @DisplayName("메뉴에는 반드시 이름이 있어야 한다.")
    @ParameterizedTest
    @NullSource
    void nameIsMandatory(String name) {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 1));

        // when
        Menu menuCreateRequest = createMenuRequest(new BigDecimal("15000"), menuGroup, menuProducts, name);

        // then
        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름에는 비속어를 사용할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"xxxx"})
    void nameContainsProfanity(String name) {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 1));

        // when
        Menu menuCreateRequest = createMenuRequest(new BigDecimal("15000"), menuGroup, menuProducts, name);

        // then
        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에는 반드시 가격이 있어야 한다.")
    @Test
    void priceIsMandatory() {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 1));

        // when
        Menu menuCreateRequest = createMenuRequest(null, menuGroup, menuProducts, "후라이드치킨");

        // then
        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격은 0보다 클 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    void negativePrice(String price) {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 1));

        // when
        Menu menuCreateRequest = createMenuRequest(new BigDecimal(price), menuGroup, menuProducts, "후라이드치킨");

        // then
        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 변경할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"15000:14000"}, delimiter = ':')
    void changePrice(String beforePrice, String afterPrice) {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal(beforePrice));
        Product product = productService.create(productRequest);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 1));

        Menu menuCreateRequest = createMenuRequest(new BigDecimal(beforePrice), menuGroup, menuProducts, "후라이드치킨");
        Menu menu = menuService.create(menuCreateRequest);

        // when
        BigDecimal beforeMenuPrice = menu.getPrice();
        Menu changePriceRequest = new Menu();
        changePriceRequest.setPrice(new BigDecimal(afterPrice));
        menu = menuService.changePrice(menu.getId(), changePriceRequest);
        BigDecimal afterMenuPrice = menu.getPrice();

        // then
        assertThat(beforeMenuPrice).isEqualByComparingTo(new BigDecimal(beforePrice));
        assertThat(afterMenuPrice).isEqualByComparingTo(new BigDecimal(afterPrice));
    }

    @DisplayName("메뉴의 가격은 메뉴를 구성하는 상품의 가격 합보다 클 수 없다.")
    @Test
    void priceCompareWithProducts() {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 2));

        // when
        Menu menuCreateRequest = createMenuRequest(new BigDecimal("31000"), menuGroup, menuProducts, "후라이드치킨");

        // then
        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 보여줄 수 있다.")
    @Test
    void display() {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 1));

        Menu menuCreateRequest = createMenuRequest(new BigDecimal("15000"), menuGroup, menuProducts, "후라이드치킨");
        Menu menu = menuService.create(menuCreateRequest);

        // when
        Menu displayedMenu = menuService.display(menu.getId());

        // then
        assertThat(displayedMenu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void hide() {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 1));

        Menu menuCreateRequest = createMenuRequest(new BigDecimal("15000"), menuGroup, menuProducts, "후라이드치킨");
        Menu menu = menuService.create(menuCreateRequest);

        // when
        Menu displayedMenu = menuService.hide(menu.getId());

        // then
        assertThat(displayedMenu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴는 최소 1개 이상의 상품으로 구성된다.")
    @Test
    void menuProductsAreMandatory() {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        // when
        Menu menuCreateRequest = createMenuRequest(new BigDecimal("15000"), menuGroup, Collections.emptyList(), "후라이드치킨");

        // then
        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 상품을 등록할 때, 수량은 0보다 작을 수 없다.")
    @Test
    void negativeQuantity() {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        // when
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, -1));

        Menu menuCreateRequest = createMenuRequest(new BigDecimal("15000"), menuGroup, menuProducts, "후라이드치킨");

        // then
        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 같은 상품을 중복해서 등록할 수 없다.")
    @Test
    void duplicateProducts() {
        // given
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("두마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        // when
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 1));
        menuProducts.add(createMenuProduct(product, 1));

        Menu menuCreateRequest = createMenuRequest(new BigDecimal("30000"), menuGroup, menuProducts, "후라이드치킨두마리");

        // then
        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때는 메뉴그룹을 지정해 주어야 한다.")
    @Test
    void menuGroupIsMandatory() {
        // given
        MenuGroup emptyMenuGroup = new MenuGroup();
        emptyMenuGroup.setId(UUID.randomUUID());

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        // when
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(product, 1));
        menuProducts.add(createMenuProduct(product, 1));

        Menu menuCreateRequest = createMenuRequest(new BigDecimal("30000"), emptyMenuGroup, menuProducts, "후라이드치킨두마리");

        // then
        assertThatThrownBy(() -> menuService.create(menuCreateRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    private Menu createMenuRequest(BigDecimal price, MenuGroup menuGroup, List<MenuProduct> menuProducts, String name) {
        Menu menuCreateRequest = new Menu();
        menuCreateRequest.setPrice(price);
        menuCreateRequest.setMenuGroup(menuGroup);
        menuCreateRequest.setMenuGroupId(menuGroup.getId());
        menuCreateRequest.setMenuProducts(menuProducts);
        menuCreateRequest.setName(name);
        return menuCreateRequest;
    }

    private MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
