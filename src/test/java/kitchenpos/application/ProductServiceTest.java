package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;


    @ParameterizedTest
    @NullSource
    void createWithNullPrice(BigDecimal price) {
        // given
        Product request = new Product();
        request.setName("양념치킨");
        request.setPrice(price);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> productService.create(request));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1000, -1})
    void createWithNegativePrice(int price) {
        // given
        Product request = new Product();
        request.setName("양념치킨");
        request.setPrice(BigDecimal.valueOf(price));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> productService.create(request));
    }

    @Test
    void createWithNullName() {
        // given
        Product request = new Product();
        request.setName(null);
        request.setPrice(BigDecimal.valueOf(1000));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> productService.create(request));
    }

    @Test
    void createWithProfanityName() {
        // given
        Product request = new Product();
        request.setName("미친치킨");
        request.setPrice(BigDecimal.valueOf(1000));

        when(purgomalumClient.containsProfanity("미친치킨")).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> productService.create(request));
    }

    @Test
    void createSuccessfully() {
        // given
        Product request = new Product();
        request.setName("양념치킨");
        request.setPrice(BigDecimal.valueOf(1000));

        when(purgomalumClient.containsProfanity("양념치킨")).thenReturn(false);

        // when
        Product created = productService.create(request);

        // then
        assertNotNull(created.getId());
        assertEquals("양념치킨", created.getName());
        assertEquals(BigDecimal.valueOf(1000), created.getPrice());
    }

    @Test
    void changePriceWithNullPrice() {
        // given
        Product product = createProduct("양념치킨", BigDecimal.valueOf(1000));
        Product request = new Product();
        request.setPrice(null);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> productService.changePrice(product.getId(), request));
    }

    @Test
    void changePriceWithNegativePrice() {
        // given
        Product product = createProduct("양념치킨", BigDecimal.valueOf(1000));
        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(-1000));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> productService.changePrice(product.getId(), request));
    }

    @Test
    void changePriceOfNonExistentProduct() {
        // given
        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(1000));

        // when & then
        assertThrows(NoSuchElementException.class,
                () -> productService.changePrice(UUID.randomUUID(), request));
    }

    @Test
    void changePriceAffectingMenuDisplay() {
        // given
        Product product = createProduct("양념치킨", BigDecimal.valueOf(3000));
        MenuGroup menuGroup = createMenuGroup("신메뉴");
        Menu menu = createMenu(menuGroup, product);

        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(1000));

        // when
        Product updated = productService.changePrice(product.getId(), request);

        // then
        assertEquals(BigDecimal.valueOf(1000), updated.getPrice());
        Menu updatedMenu = menuRepository.findById(menu.getId()).orElseThrow();
        assertFalse(updatedMenu.isDisplayed());
    }

    private Menu createMenu(MenuGroup menuGroup, Product product) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념치킨");
        menu.setPrice(BigDecimal.valueOf(2000));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
        menu.setMenuProducts(List.of(menuProduct));
        menuRepository.save(menu);
        return menu;
    }

    private Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        when(purgomalumClient.containsProfanity(name)).thenReturn(false);
        return productService.create(product);
    }

    private MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroupRepository.save(menuGroup);
    }
}
