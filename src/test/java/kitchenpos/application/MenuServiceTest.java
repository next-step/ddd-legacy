package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @NotNull
    private static List<MenuProduct> createMenuProducts(UUID productId, int quantity) {
        List<MenuProduct> menuProducts = new ArrayList<>();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);  // 존재하지 않는 ProductId
        menuProduct.setQuantity(quantity);
        menuProducts.add(menuProduct);
        return menuProducts;
    }

    @NotNull
    private static Menu createMenu(String name, int price, UUID menuGroupId, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    @NotNull
    private static MenuProduct createMenuProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
        return menuProduct;
    }

    @Test
    void createWithNegativePrice() {
        //given
        Menu menu = new Menu();
        menu.setName("간장치킨");
        menu.setPrice(BigDecimal.valueOf(-1));

        //when
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithNoMenuGroup() {
        //given
        Menu menu = createMenu("간장치킨", 100, UUID.randomUUID(), new ArrayList<>());

        //when
        assertThrows(NoSuchElementException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithNonMatchingProducts() {
        //given
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴", UUID.randomUUID());
        Menu menu = createMenu("간장치킨", 100, menuGroup.getId(), new ArrayList<>());

        //when
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithNegativeQuantity() {
        //given
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴", UUID.randomUUID());
        Product product = createProduct("간장치킨", 100);
        Menu menu = createMenu("간장치킨", 100, menuGroup.getId(), createMenuProducts(product.getId(), -1));

        //when
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithPriceHigherThanSum() {
        //given
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴", UUID.randomUUID());
        Product product = createProduct("후라이드", 100);
        Menu menu = createMenu("후라이드", 1000, menuGroup.getId(), createMenuProducts(product.getId(), 1));

        //when
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithProfanityName() {
        //given
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴", UUID.randomUUID());
        Menu menu = createMenu("미친치킨", 100, menuGroup.getId(), new ArrayList<>());

        //when
        when(purgomalumClient.containsProfanity("미친치킨")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createSuccessfully() {
        //given
        MenuGroup menuGroup = createMenuGroup("신메뉴", UUID.randomUUID());
        Product product = createProduct("양념치킨", 100);
        Menu menu = createMenu("양념치킨", 100, menuGroup.getId(), createMenuProducts(product.getId(), 1));

        //when
        when(purgomalumClient.containsProfanity("양념치킨")).thenReturn(false);
        Menu created = menuService.create(menu);

        //then
        assertNotNull(created.getId());
        assertEquals("양념치킨", created.getName());
        assertEquals(BigDecimal.valueOf(100), created.getPrice());
        assertEquals(menuGroup.getId(), created.getMenuGroup().getId());
        assertEquals(1, created.getMenuProducts().size());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1000, -1})
    void changePriceWithNegativePrice(int price) {
        //given
        UUID menuId = UUID.randomUUID();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(price));

        //when
        assertThrows(IllegalArgumentException.class,
                () -> menuService.changePrice(menuId, request));
    }

    @Test
    void changePriceWithNonExistentMenu() {
        //given
        UUID menuId = UUID.randomUUID();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(100));

        //when
        assertThrows(NoSuchElementException.class,
                () -> menuService.changePrice(menuId, request));
    }

    @Test
    void changePriceWithPriceHigherThanSum() {
        //given
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴", UUID.randomUUID());
        Product product = createProduct("양념치킨", 50);
        Menu menu = createMenu(menuGroup, product);
        menuRepository.save(menu);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(1000));

        //when
        assertThrows(IllegalArgumentException.class,
                () -> menuService.changePrice(menu.getId(), request));
    }

    @Test
    void changePriceSuccessfully() {
        //given
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴", UUID.randomUUID());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroup(menuGroup);

        Product product = createProduct("양념치킨", 50);

        MenuProduct menuProduct = createMenuProduct(product);
        menu.setMenuProducts(List.of(menuProduct));
        menuRepository.save(menu);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(50));

        //when
        Menu updated = menuService.changePrice(menu.getId(), request);

        //then
        assertEquals(BigDecimal.valueOf(50), updated.getPrice());
    }

    @Test
    void displayNonExistentMenu() {
        //given
        UUID menuId = UUID.randomUUID();

        //when
        assertThrows(NoSuchElementException.class, () -> menuService.display(menuId));
    }

    @Test
    void displayWithPriceHigherThanSum() {
        //given
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴", UUID.randomUUID());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroup(menuGroup);

        Product product = createProduct("양념치킨", 50);

        MenuProduct menuProduct = createMenuProduct(product);
        menu.setMenuProducts(List.of(menuProduct));
        menuRepository.save(menu);

        //when
        assertThrows(IllegalStateException.class, () -> menuService.display(menu.getId()));
    }

    @Test
    void displaySuccessfully() {
        //given
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴", UUID.randomUUID());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(BigDecimal.valueOf(50));
        menu.setName("양념치킨");
        menu.setDisplayed(false);
        menu.setMenuGroup(menuGroup);

        Product product = createProduct("양념치킨", 50);

        MenuProduct menuProduct = createMenuProduct(product);
        menu.setMenuProducts(List.of(menuProduct));
        menuRepository.save(menu);

        //when
        Menu displayed = menuService.display(menu.getId());

        //then
        assertTrue(displayed.isDisplayed());
    }

    @Test
    void hideNonExistentMenu() {
        //given
        UUID menuId = UUID.randomUUID();

        //when
        assertThrows(NoSuchElementException.class, () -> menuService.hide(menuId));
    }

    @Test
    void hideSuccessfully() {
        //given
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴", UUID.randomUUID());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(BigDecimal.valueOf(50));
        menu.setDisplayed(true);
        menu.setMenuGroup(menuGroup);  // MenuGroup 설정
        menu.setName("양념치킨");     // name도 필요할 수 있음
        menuRepository.save(menu);

        //when
        Menu hidden = menuService.hide(menu.getId());

        //then
        assertFalse(hidden.isDisplayed());
    }

    private Menu createMenu(MenuGroup menuGroup, Product product) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(2000));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);

        MenuProduct menuProduct = createMenuProduct(product);
        menu.setMenuProducts(List.of(menuProduct));
        menuRepository.save(menu);

        return menu;
    }

    private MenuGroup createMenuGroup(String name, UUID menuGroupId) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(menuGroupId);
        menuGroupRepository.save(menuGroup);

        return menuGroup;
    }

    @NotNull
    private Product createProduct(String name, int price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        productRepository.save(product);

        return product;
    }
}
