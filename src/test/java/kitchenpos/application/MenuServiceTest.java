package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.testfixture.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    MenuRepository menuRepository = new InMemoryMenuRepository();

    MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();


    ProductRepository productRepository = new InMemoryProductRepository();

    PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @Test
    void create() {

        //given
        MenuGroup menuGroup = MenuGroupTestFixture.createMenuGroup(UUID.randomUUID(), "치킨류");
        menuGroupRepository.save(menuGroup);
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 18000L);
        productRepository.save(product);

        Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, product);
        request.setMenuGroup(menuGroup);
        request.setMenuGroupId(menuGroup.getId());

        MenuProduct menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
        request.setMenuProducts(List.of(menuProduct));
        //when
        Menu response = menuService.create(request);

        //then
        assertEquals(request.getName(), response.getName());
        assertEquals(request.isDisplayed(), response.isDisplayed());


    }

    @Test
    void mustHaveMenuGroup() {
        //given
        MenuGroup menuGroup = MenuGroupTestFixture.createMenuGroup(UUID.randomUUID(), "치킨류");
        menuGroupRepository.save(menuGroup);
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 18000L);
        productRepository.save(product);

        Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, product);

        MenuProduct menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
        request.setMenuProducts(List.of(menuProduct));

        //when then
        assertThrows(NoSuchElementException.class, () -> menuService.create(request));

    }

    @Test
    void mustHaveProduct() {
        //given
        MenuGroup menuGroup = MenuGroupTestFixture.createMenuGroup(UUID.randomUUID(), "치킨류");
        menuGroupRepository.save(menuGroup);
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 18000L);
        productRepository.save(product);

        Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, new Product());
        request.setMenuGroup(menuGroup);
        request.setMenuGroupId(menuGroup.getId());

        //when then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
    }

    @Test
    void mustSameSizeOfProduct() {
        //given
        MenuGroup menuGroup = MenuGroupTestFixture.createMenuGroup(UUID.randomUUID(), "치킨류");
        menuGroupRepository.save(menuGroup);
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 18000L);
        productRepository.save(product);
        Product product2 = ProductTestFixture.createProduct(UUID.randomUUID(), "양념치킨", 19000L);

        Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, new Product());
        request.setMenuGroup(menuGroup);
        request.setMenuGroupId(menuGroup.getId());
        MenuProduct menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
        MenuProduct menuProduct2 = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product2);
        request.setMenuProducts(List.of(menuProduct, menuProduct2));

        //when then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
    }


    @Test
    void changePrice() {
        //given
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 18000L);
        Menu menu = MenuTestFixture.createMenu(UUID.randomUUID(), "후라이드치킨", 18000L, true, product);
        MenuProduct menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
        menu.setMenuProducts(List.of(menuProduct));
        productRepository.save(product);
        menuRepository.save(menu);

        Menu request = MenuTestFixture.createMenu(menu.getId(), menu.getName(), 17000L, menu.isDisplayed(), product);
        request.setMenuProducts(menu.getMenuProducts());


        //when
        Menu response = menuService.changePrice(request.getId(), request);

        //then
        assertEquals(BigDecimal.valueOf(17000), response.getPrice());

    }

    @Test
    void canNotMinusPrice() {
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 18000L);
        Menu menu = MenuTestFixture.createMenu(UUID.randomUUID(), "후라이드치킨", 18000L, true, product);
        productRepository.save(product);
        menuRepository.save(menu);

        Menu request = MenuTestFixture.createMenu(menu.getId(), menu.getName(), -1000L, menu.isDisplayed(), product);

        //when then
        assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(request.getId(), request));

    }

    @Test
    void display() {

        //given
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 11000L);
        UUID menuId = UUID.randomUUID();
        Menu request = MenuTestFixture.createMenu(menuId, "menu1", 10000L, true, product);
        MenuProduct menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
        request.setMenuProducts(List.of(menuProduct));
        menuRepository.save(request);
        // when
        Menu response = menuService.display(request.getId());

        // then
        assertEquals(true, response.isDisplayed());

    }

    @Test
    void mustHaveMenu() {

        //given
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 11000L);
        UUID menuId = UUID.randomUUID();
        Menu request = MenuTestFixture.createMenu(menuId, "menu1", 10000L, true, product);
        // when then
        assertThrows(NoSuchElementException.class, () -> menuService.display(menuId));
    }

    @Test
    void hide() {

        //given
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 11000L);
        UUID menuId = UUID.randomUUID();
        Menu request = MenuTestFixture.createMenu(menuId, "menu1", 10000L, true, product);
        menuRepository.save(request);
        // when
        Menu response = menuService.hide(request.getId());

        // then
        assertEquals(false, response.isDisplayed());

    }

    @Test
    void findAll() {

        //given
        Menu menu1 = MenuTestFixture.createMenu(UUID.randomUUID(), "menu1", 10000L, true, new Product());

        Menu menu2 = MenuTestFixture.createMenu(UUID.randomUUID(), "menu2", 14000L, true, new Product());
        menuRepository.save(menu1);
        menuRepository.save(menu2);

        //when
        List<Menu> response = menuService.findAll();

        //then
        assertEquals(2, response.size());
    }

}