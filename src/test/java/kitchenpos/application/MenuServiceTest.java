package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuGroupRepository;
import kitchenpos.application.fake.FakeMenuRepository;
import kitchenpos.application.fake.FakeProductRepository;
import kitchenpos.application.fake.FakeProfanityClient;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.ProfanityClient;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuServiceTest {

    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
    private final ProductRepository productRepository = new FakeProductRepository();
    private final ProfanityClient profanityClient = new FakeProfanityClient();

    private final MenuService service = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);


    @Test
    @DisplayName("메뉴는 이름, 가격, 메뉴 그룹, 노출 여부, 메뉴에 등록할 상품들을 입력하여 등록할 수 있다.")
    void create() {
        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 5000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Menu savedMenu = service.create(menu);

        assertThat(savedMenu.getId()).isNotNull();
        assertThat(savedMenu.getName()).isEqualTo(menu.getName());
        assertThat(savedMenu.getPrice()).isEqualTo(menu.getPrice());
        assertThat(savedMenu.isDisplayed()).isEqualTo(menu.isDisplayed());
    }

    @Test
    @DisplayName("메뉴을 등록 할 때 가격은 비어있거나 0이하의 수를 입력할 수 없다.")
    void create_not_empty_price() {
        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", -1L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        assertThatThrownBy(() -> service.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("메뉴을 등록 할 때 이름은 비어있거나 욕설이 포함될 수 없다.")
    @MethodSource("nameMethodSource")
    void create_not_empty_name(String name) {

        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu(name, 5000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        assertThatThrownBy(() -> service.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹은 필수로 입력해야 한다.")
    void create_menu_group() {

        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 5000L, new MenuGroup(), savedMenuProduct);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        assertThatThrownBy(() -> service.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("상품은 수량을 포함하며 0미만의 수를 입력할 수 없다.")
    void create_product_quantity_zero() {

        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), -1),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 5000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        assertThatThrownBy(() -> service.create(menu))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("메뉴를 등록할 때 입력한 가격이 상품 가격의 합보다 클 수 없다.")
    void create_product_price() {
        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 150000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        assertThatThrownBy(() -> service.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격을 수정할 수 있다.")
    void changePrice() {

        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 15000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Menu savedMenu = service.create(menu);
        savedMenu.changedPrice(20000L);

        Menu changedMenu = service.changePrice(savedMenu.getId(), savedMenu);

        assertThat(savedMenu.getPrice()).isEqualTo(changedMenu.getPrice());
    }

    @Test
    @DisplayName("메뉴의 가격을 수정 할 때 가격은 비어있거나 0이하의 수를 입력할 수 없다.")
    void change_price_not_empty_price() {

        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 15000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Menu savedMenu = service.create(menu);
        savedMenu.changedPrice(-1L);

        assertThatThrownBy(() -> service.changePrice(savedMenu.getId(), savedMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("수정할 가격이 메뉴로 등록한 상품 가격의 합보다 클 수 없다.")
    void change_price_product_price() {

        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 15000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Menu savedMenu = service.create(menu);
        savedMenu.changedPrice(2000000L);

        assertThatThrownBy(() -> service.changePrice(savedMenu.getId(), savedMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("메뉴를 고객에게 노출 시킬 수 있다.")
    void display() {

        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 15000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Menu savedMenu = service.create(menu);

        Menu displayedMenu = service.display(savedMenu.getId());

        assertThat(displayedMenu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("노출할 메뉴의 가격이 메뉴로 등록한 상품 가격의 합보다 클 수 없다.")
    void display_price() {
        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 15000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Menu savedMenu = service.create(menu);

        product1.changePrice(10L);
        productRepository.save(product1);

        assertThatThrownBy(() -> service.display(savedMenu.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("메뉴를 고객에게 숨길 수 있다.")
    void hide() {
        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu = createMenu("메뉴 이름", 15000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Menu savedMenu = service.create(menu);

        Menu hidedMenu = service.hide(savedMenu.getId());

        assertThat(hidedMenu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("등록한 모든 메뉴를 조회할 수 있다.")
    void findAll() {

        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));

        Menu menu1 = createMenu("메뉴 이름1", 5000L, menuGroup, savedMenuProduct);
        Menu menu2 = createMenu("메뉴 이름2", 50000L, menuGroup, savedMenuProduct);

        menuGroupRepository.save(menuGroup);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        List<Menu> savedProducts = Lists.list(service.create(menu1), service.create(menu2));
        List<Menu> menus = service.findAll();

        assertThat(menus).containsAll(savedProducts);
    }


    private MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup(name);
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }


    private Product createProduct(String name, long price) {
        Product product = new Product(name, BigDecimal.valueOf(price));
        product.setId(UUID.randomUUID());
        return product;
    }

    private static Menu createMenu(String name, long price, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    static Stream<String> nameMethodSource() {
        return Stream.of("비속어", null);
    }
}