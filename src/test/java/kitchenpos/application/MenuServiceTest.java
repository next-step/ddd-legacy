package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Test;
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

    @Test
    void createWithNegativePrice() {
        //given
        Menu menu = new Menu();
        menu.setName("Test");
        menu.setPrice(BigDecimal.valueOf(-1));

        //when
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithNoMenuGroup() {
        //given
        Menu menu = new Menu();
        menu.setName("Test");
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroupId(UUID.randomUUID());

        //when
        assertThrows(NoSuchElementException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithNonMatchingProducts() {
        //given
        Menu menu = new Menu();
        menu.setName("Test");
        menu.setPrice(BigDecimal.valueOf(100));
        UUID menuGroupId = UUID.randomUUID();
        menu.setMenuGroupId(menuGroupId);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("Test");
        menuGroup.setId(menuGroupId);
        menuGroupRepository.save(menuGroup);

        List<MenuProduct> menuProducts = new ArrayList<>();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());  // 존재하지 않는 ProductId
        menuProduct.setQuantity(1);
        menuProducts.add(menuProduct);
        menu.setMenuProducts(menuProducts);

        //when
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithNegativeQuantity() {
        //given
        Menu menu = new Menu();
        menu.setName("Test");
        menu.setPrice(BigDecimal.valueOf(100));
        UUID menuGroupId = UUID.randomUUID();
        menu.setMenuGroupId(menuGroupId);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("Test");
        menuGroup.setId(menuGroupId);
        menuGroupRepository.save(menuGroup);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(100));
        productRepository.save(product);

        List<MenuProduct> menuProducts = new ArrayList<>();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(-1);
        menuProducts.add(menuProduct);
        menu.setMenuProducts(menuProducts);

        //when
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithPriceHigherThanSum() {
        //given
        Menu menu = new Menu();
        menu.setName("후라이드");
        menu.setPrice(BigDecimal.valueOf(1000));
        UUID menuGroupId = UUID.randomUUID();
        menu.setMenuGroupId(menuGroupId);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("한마리 메뉴");
        menuGroup.setId(menuGroupId);
        menuGroupRepository.save(menuGroup);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(100));
        productRepository.save(product);

        List<MenuProduct> menuProducts = new ArrayList<>();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1);
        menuProducts.add(menuProduct);
        menu.setMenuProducts(menuProducts);

        //when
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createWithProfanityName() {
        //given
        Menu menu = new Menu();
        menu.setName("미친치킨");
        menu.setPrice(BigDecimal.valueOf(100));
        UUID menuGroupId = UUID.randomUUID();
        menu.setMenuGroupId(menuGroupId);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("한마리 메뉴");
        menuGroup.setId(menuGroupId);
        menuGroupRepository.save(menuGroup);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("미친치킨");
        product.setPrice(BigDecimal.valueOf(100));
        productRepository.save(product);

        List<MenuProduct> menuProducts = new ArrayList<>();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1);
        menuProducts.add(menuProduct);
        menu.setMenuProducts(menuProducts);

        //when
        when(purgomalumClient.containsProfanity("미친치킨")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @Test
    void createSuccessfully() {
        //given
        Menu menu = new Menu();
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(100));
        UUID menuGroupId = UUID.randomUUID();
        menu.setMenuGroupId(menuGroupId);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("신메뉴");
        menuGroup.setId(menuGroupId);
        menuGroupRepository.save(menuGroup);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(100));
        productRepository.save(product);

        List<MenuProduct> menuProducts = new ArrayList<>();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1);
        menuProducts.add(menuProduct);
        menu.setMenuProducts(menuProducts);

        //when
        when(purgomalumClient.containsProfanity("양념치킨")).thenReturn(false);
        Menu created = menuService.create(menu);

        //then
        assertNotNull(created.getId());
        assertEquals("양념치킨", created.getName());
        assertEquals(BigDecimal.valueOf(100), created.getPrice());
        assertEquals(menuGroupId, created.getMenuGroup().getId());
        assertEquals(1, created.getMenuProducts().size());
    }

    @Test
    void changePriceWithNegativePrice() {
        //given
        UUID menuId = UUID.randomUUID();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(-1));

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
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroup(menuGroup);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(50));
        productRepository.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
        menu.setMenuProducts(List.of(menuProduct));
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
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroup(menuGroup);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(50));
        productRepository.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
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
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroup(menuGroup);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(50));
        productRepository.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
        menu.setMenuProducts(List.of(menuProduct));
        menuRepository.save(menu);

        //when
        assertThrows(IllegalStateException.class, () -> menuService.display(menu.getId()));
    }

    @Test
    void displaySuccessfully() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(BigDecimal.valueOf(50));
        menu.setName("양념치킨");
        menu.setDisplayed(false);
        menu.setMenuGroup(menuGroup);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(50));
        productRepository.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
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
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리 메뉴");
        menuGroupRepository.save(menuGroup);

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
}
