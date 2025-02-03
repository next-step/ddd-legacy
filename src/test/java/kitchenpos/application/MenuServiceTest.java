package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@Transactional
@SpringBootTest
class MenuServiceTest {

    private static final UUID BURGER_PRODUCT_ID = UUID.randomUUID();
    private static final UUID SIDE_PRODUCT_ID = UUID.randomUUID();
    private static final UUID COKE_PRODUCT_ID = UUID.randomUUID();
    private static final UUID MENU_GROUP_ID = UUID.randomUUID();

    @Autowired
    private MenuService menuService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        productRepository.save(createProduct(BURGER_PRODUCT_ID, "치킨버거", new BigDecimal(7000)));
        productRepository.save(createProduct(SIDE_PRODUCT_ID, "감자튀김", new BigDecimal(2000)));
        productRepository.save(createProduct(COKE_PRODUCT_ID, "콜라", new BigDecimal(2000)));

        menuGroupRepository.save(createMenuGroup(MENU_GROUP_ID, "세트메뉴"));
    }

    //region [메뉴 등록]
    @DisplayName("단일 상품으로 메뉴를 등록할 수 있다")
    @Test
    void createMenuByProduct() {
        MenuProduct chickenBurger = createMenuProduct(BURGER_PRODUCT_ID, 1);
        Menu menu = createMenu(MENU_GROUP_ID, "치킨버거", new BigDecimal(7000), List.of(chickenBurger));

        Menu resultMenu = menuService.create(menu);

        assertThat(resultMenu.getName()).isEqualTo("치킨버거");
        assertThat(resultMenu.getPrice()).isEqualTo(new BigDecimal(7000));
        assertThat(resultMenu.isDisplayed()).isFalse();
        assertThat(resultMenu.getMenuProducts()).hasSize(1);
        assertThat(resultMenu.getMenuGroup()).isNotNull();
        assertThat(resultMenu.getMenuGroup().getId()).isEqualTo(MENU_GROUP_ID);
    }

    @DisplayName("여러 상품으로 조합하여 하나의 메뉴를 등록할 수 있다")
    @Test
    void createMenuByProducts() {
        //given
        MenuProduct chickenBurger = createMenuProduct(BURGER_PRODUCT_ID, 1);
        MenuProduct side = createMenuProduct(SIDE_PRODUCT_ID, 1);
        MenuProduct coke = createMenuProduct(COKE_PRODUCT_ID, 1);

        Menu menu = createMenu(
                MENU_GROUP_ID,
                "치킨버거세트",
                new BigDecimal(10000),
                List.of(chickenBurger, side, coke)
        );
        //when
        Menu resultMenu = menuService.create(menu);
        //then
        assertThat(resultMenu.getName()).isEqualTo("치킨버거세트");
        assertThat(resultMenu.getPrice()).isEqualTo(new BigDecimal(10000));
        assertThat(resultMenu.getMenuProducts()).hasSize(3);
        assertThat(resultMenu.getMenuGroup()).isNotNull();
        assertThat(resultMenu.getMenuGroup().getId()).isEqualTo(MENU_GROUP_ID);
    }

    @DisplayName("각 메뉴를 구성하는 상품의 수량은 0이상이어야 한다")
    @Test
    void validateMenuProductQuantity() {
        MenuProduct chickenBurger = createMenuProduct(BURGER_PRODUCT_ID, 1);
        MenuProduct coke = createMenuProduct(COKE_PRODUCT_ID, -1);
        Menu menu = createMenu(
                MENU_GROUP_ID,
                "치킨버거+음료",
                new BigDecimal(9000),
                List.of(chickenBurger, coke)
        );

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴명은 반드시 입력되어야 한다")
    @ParameterizedTest
    @NullSource
    void notNullName(String nullName) {
        MenuProduct chickenBurger = createMenuProduct(BURGER_PRODUCT_ID, 1);
        Menu menu = createMenu(MENU_GROUP_ID, nullName, new BigDecimal(7000), List.of(chickenBurger));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴명에 비속어가 있으면 등록할 수 없다")
    @Test
    void validateMenuName() {
        Mockito.when(purgomalumClient.containsProfanity("bad word")).thenReturn(true);

        MenuProduct chickenBurger = createMenuProduct(BURGER_PRODUCT_ID, 1);
        Menu menu = createMenu(MENU_GROUP_ID, "bad word", new BigDecimal(7000), List.of(chickenBurger));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("가격은 반드시 입력되어야 한다")
    @ParameterizedTest
    @NullSource
    void notNullPrice(BigDecimal nullPrice) {
        MenuProduct chickenBurger = createMenuProduct(BURGER_PRODUCT_ID, 1);
        Menu menu = createMenu(MENU_GROUP_ID, "치킨버거", nullPrice, List.of(chickenBurger));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 가격은 0원 이상이어야 한다")
    @Test
    void nonNegativeMenuPrice() {
        MenuProduct chickenBurger = createMenuProduct(BURGER_PRODUCT_ID, 1);
        Menu menu = createMenu(MENU_GROUP_ID, "치킨버거", new BigDecimal(-1), List.of(chickenBurger));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 가격이 포함된 상품 가격 총합과 동일하거나 할인된 가격일 경우만 등록 가능하다")
    @Test
    void validateMenuPrice() {
        //given
        MenuProduct chickenBurger = createMenuProduct(BURGER_PRODUCT_ID, 1);    //7000원
        MenuProduct side = createMenuProduct(SIDE_PRODUCT_ID, 1);   //2000원
        MenuProduct coke = createMenuProduct(COKE_PRODUCT_ID, 1);   //2000원

        Menu menu = createMenu(
                MENU_GROUP_ID,
                "치킨버거세트",
                new BigDecimal(11001),
                List.of(chickenBurger, side, coke)
        );
        //when, then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu));
    }
    //endregion

    private Product createProduct(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private Menu createMenu(UUID menuGroupId, String name, BigDecimal price, List<MenuProduct> products) {
        Menu menu = new Menu();
        menu.setMenuGroupId(menuGroupId);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuProducts(products);
        return menu;
    }

    private MenuProduct createMenuProduct(UUID productId, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }
}
