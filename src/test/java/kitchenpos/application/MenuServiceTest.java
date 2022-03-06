package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
class MenuServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private PurgomalumClient purgomalumClient;

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        purgomalumClient = new FakePurgomalumClient(false);
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴를 생성할 수 있다.")
    @Test
    void create_with_valid_attribute() {
        Product product = productRepository.save(createProduct("test1", 1000));
        List<MenuProduct> menuProducts = createMenuProduct(Collections.singletonList(product), 1);
        MenuGroup menuGroup = findAnyMenuGroup();
        Menu request = createMenu(menuProducts, menuGroup, new MenuMainAttribute("menu1", 1000, true));

        Menu actual = menuService.create(request);

        assertThat(actual).isNotNull();
    }

    @DisplayName("메뉴의 그룹정보가 존재해야한다.")
    @Test
    void create_with_no_menu_group() {
        final Product product = productRepository.save(createProduct("test1", 1000));
        final List<MenuProduct> menuProducts = createMenuProduct(Collections.singletonList(product), 1);
        final String notFoundUUID = "06fe3514-a8a6-48ed-85e6-e7296d0e1000";
        final MenuGroup menuGroup = menuGroupRepository.getById(UUID.fromString(notFoundUUID));
        final Menu request = createMenu(menuProducts, menuGroup, new MenuMainAttribute("menu1", 1000, true));

        assertThatCode(() ->
                menuService.create(request)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴의 가격은 0보다 작을 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1000, -5000})
    void create_with_negative_price(int price) {
        final Product product = productRepository.save(createProduct("test1", 1000));
        final List<MenuProduct> menuProducts = createMenuProduct(Collections.singletonList(product), 1);
        final MenuGroup menuGroup = findAnyMenuGroup();
        final Menu request = createMenu(menuProducts, menuGroup, new MenuMainAttribute("menu1", price, true));

        assertThatCode(() ->
                menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴 상품들의 합보다 클 수 없다.")
    @Test
    void create_with_price_more_than_price_sum() {
        final Product product = productRepository.save(createProduct("test1", 100));
        final List<MenuProduct> menuProducts = createMenuProduct(Collections.singletonList(product), 1);
        final MenuGroup menuGroup = findAnyMenuGroup();
        final Menu request = createMenu(menuProducts, menuGroup, new MenuMainAttribute("menu1", 2000, true));

        assertThatCode(() ->
                menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품의 정보가 존재해야한다")
    @Test
    void create_with_not_exist_product() {
        final Product product = createProduct("test1", 100);
        final List<MenuProduct> menuProducts = createMenuProduct(Collections.singletonList(product), 1);
        final MenuGroup menuGroup = findAnyMenuGroup();
        final Menu request = createMenu(menuProducts, menuGroup, new MenuMainAttribute("menu1", 2000, true));

        assertThatCode(() ->
                menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품 정보가 존재해야한다. ")
    @ParameterizedTest
    @NullAndEmptySource
    void create_with_no_menu_product(List<MenuProduct> menuProducts) {
        final MenuGroup menuGroup = findAnyMenuGroup();
        final Menu request = createMenu(menuProducts, menuGroup, new MenuMainAttribute("menu1", 1000, true));

        assertThatCode(() ->
                menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품 갯수 해당하는 상품과 갯수가 일치해야한다.")
    @Test
    void create_with_different_product_size() {
        final Product product1 = productRepository.save(createProduct("test1", 1000));
        final Product product2 = createProduct("test2", 1000);
        final List<MenuProduct> menuProducts = createMenuProduct(Arrays.asList(product1, product2), 1);
        final MenuGroup menuGroup = findAnyMenuGroup();
        final Menu request = createMenu(menuProducts, menuGroup, new MenuMainAttribute("menu1", 2000, true));

        assertThatCode(() ->
                menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품의 수량은 0보다 작을 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    void create_with_negative_menu_product_quantity(int quantity) {
        final Product product1 = productRepository.save(createProduct("test1", 1000));
        final Product product2 = createProduct("test2", 1000);
        final List<MenuProduct> menuProducts = createMenuProduct(Arrays.asList(product1, product2), quantity);
        final MenuGroup menuGroup = findAnyMenuGroup();
        final Menu request = createMenu(menuProducts, menuGroup, new MenuMainAttribute("menu1", 2000, true));

        assertThatCode(() ->
                menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private MenuGroup findAnyMenuGroup() {
        return menuGroupRepository.findAll()
                .stream()
                .findAny()
                .orElseThrow(EntityNotFoundException::new);
    }


    private Product createProduct(String givenProductName, int givenPrice) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(givenProductName);
        product.setPrice(BigDecimal.valueOf(givenPrice));
        return product;
    }

    private List<MenuProduct> createMenuProduct(List<Product> products, int quantity) {
        List<MenuProduct> result = new ArrayList<>();
        for (Product product : products) {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(quantity);
            result.add(menuProduct);
        }
        return Collections.unmodifiableList(result);
    }

    public Menu createMenu(List<MenuProduct> menuProduct, MenuGroup menuGroup, MenuMainAttribute menuMainAttribute) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuProducts(menuProduct);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setPrice(BigDecimal.valueOf(menuMainAttribute.getPrice()));
        menu.setName(menuMainAttribute.getName());
        menu.setDisplayed(menuMainAttribute.isDisplay());
        return menu;
    }

    static class MenuMainAttribute {
        private final String name;
        private final int price;
        private final boolean display;

        public MenuMainAttribute(String name, int price, boolean display) {
            this.name = name;
            this.price = price;
            this.display = display;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public boolean isDisplay() {
            return display;
        }
    }
}