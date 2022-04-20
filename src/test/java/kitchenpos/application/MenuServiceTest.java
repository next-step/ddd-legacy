package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.exception.EmptyOrProfanityNameException;
import kitchenpos.exception.PriceLessThanZeroException;
import kitchenpos.exception.NotTheSameSizeException;
import kitchenpos.exception.QuantityLessThenZeroException;
import kitchenpos.infra.FakeProfanityClient;
import kitchenpos.infra.ProfanityClient;
import kitchenpos.repository.InMemoryMenuGroupRepository;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryProductRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;

@DisplayName("[메뉴]")
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final ProfanityClient profanityClient = new FakeProfanityClient();

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        this.menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
    }

    @ValueSource(strings = {"-1", "-100"})
    @ParameterizedTest
    @DisplayName("메뉴의 가격은 0보다 크거나 같아야 한다")
    public void menuPriceLessThanZeroTest(final BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);

        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(PriceLessThanZeroException.class);
    }

    @Test
    @DisplayName("메뉴는 메뉴그룹에 속해있지 않다면 에러를 발생한다.")
    public void menuInMenuGroupTest() {

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menu.setMenuGroupId(UUID.randomUUID());

        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("하나 이상의 메뉴 상품이없다면 에러를 발생시킨다.")
    public void mustBeAtLeastOneProductTest() {

        final UUID menuGroupId = UUID.randomUUID();

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menu.setMenuGroupId(menuGroupId);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("한마리 메뉴");

        menuGroupRepository.save(menuGroup);

        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("요청한 메뉴 상품의 정보가 존재하지 않으면 안된다.")
    public void menuGroupEqualsToProduct() {

        final UUID menuGroupId = UUID.randomUUID();

        Product 후라이드 = createProduct(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(18_000L));
        Product 양념 = createProduct(UUID.randomUUID(), "양념", BigDecimal.valueOf(20_000L));

        MenuProduct menuProduct = createMenuProduct(1, 후라이드);
        MenuProduct menuProduct2 = createMenuProduct(1, 양념);

        Menu menu = createMenu(BigDecimal.valueOf(18_000L), menuGroupId, Arrays.asList(menuProduct, menuProduct2));
        MenuGroup menuGroup = createMenuGroup(menuGroupId, "한마리 메뉴");

        menuGroupRepository.save(menuGroup);
        productRepository.save(후라이드);

        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NotTheSameSizeException.class);
    }

    @Test
    @DisplayName("요청한 메뉴상품의 갯수는 0보다 작을 수 없다")
    public void menuProductQuantityTest() {
        final UUID menuGroupId = UUID.randomUUID();

        Product 후라이드 = createProduct(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(18_000L));
        MenuProduct menuProduct = createMenuProduct(-1, 후라이드);
        Menu menu = createMenu(BigDecimal.valueOf(18_000L), menuGroupId, Arrays.asList(menuProduct));
        MenuGroup menuGroup = createMenuGroup(menuGroupId, "한마리 메뉴");

        menuGroupRepository.save(menuGroup);
        productRepository.save(후라이드);
        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(QuantityLessThenZeroException.class);
    }

    @Test
    @DisplayName("메뉴 이름이 반드시 있어야한다.")
    public void requiredMenuName() {
        final UUID menuGroupId = UUID.randomUUID();
        Product 후라이드 = createProduct(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(18_000L));
        MenuProduct menuProduct = createMenuProduct(1, 후라이드);
        Menu menu = createMenu(BigDecimal.valueOf(18_000L), menuGroupId, Arrays.asList(menuProduct));
        MenuGroup menuGroup = createMenuGroup(menuGroupId, "한마리 메뉴");

        menuGroupRepository.save(menuGroup);
        productRepository.save(후라이드);

        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(EmptyOrProfanityNameException.class);
    }

    @Test
    @DisplayName("메뉴 이름에는 비속어가 포함될 수 없다.")
    public void notContainsProfanityMenuName() {
        final UUID menuGroupId = UUID.randomUUID();
        Product 후라이드 = createProduct(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(18_000L));
        MenuProduct menuProduct = createMenuProduct(1, 후라이드);
        Menu menu = createMenu("욕설",BigDecimal.valueOf(18_000L), menuGroupId, Arrays.asList(menuProduct));
        MenuGroup menuGroup = createMenuGroup(menuGroupId, "한마리 메뉴");

        menuGroupRepository.save(menuGroup);
        productRepository.save(후라이드);

        AssertionsForClassTypes.assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(EmptyOrProfanityNameException.class);
    }

    @Test
    @DisplayName("메뉴를 등록한다")
    public void createMenu() {
        final UUID menuGroupId = UUID.randomUUID();
        Product 후라이드 = createProduct(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(18_000L));
        MenuProduct menuProduct = createMenuProduct(1, 후라이드);
        Menu menu = createMenu("후라이드",BigDecimal.valueOf(18_000L), menuGroupId, Arrays.asList(menuProduct));
        MenuGroup menuGroup = createMenuGroup(menuGroupId, "한마리 메뉴");

        menuGroupRepository.save(menuGroup);
        productRepository.save(후라이드);

        final Menu actual = menuService.create(menu);

        Assertions.assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menu.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(menu.getPrice())
        );
    }

    @Test
    @DisplayName("메뉴의 가격이 각 메뉴상품의 갯수의 합보다 작다면 노출이 된다.")
    public void displayTest() {

        final UUID menuGroupId = UUID.randomUUID();
        Product 후라이드 = createProduct(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(18_000L));
        MenuProduct menuProduct = createMenuProduct(2, 후라이드);
        Menu menu = createMenu(UUID.randomUUID(),"후라이드",BigDecimal.valueOf(18_000L), menuGroupId, Arrays.asList(menuProduct));

        menuRepository.save(menu);

        Menu actual = menuService.display(menu.getId());
        assertThat(actual.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴를 숨김처리 할 수 있다")
    public void hideTest() {

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);

        menuRepository.save(menu);
        Menu display = menuService.hide(menu.getId());

        assertThat(display.isDisplayed()).isFalse();
    }

    private Product createProduct(final UUID uuid, final String name, final BigDecimal price) {
        Product product = new Product();
        product.setId(uuid);
        product.setName(name);
        product.setPrice(price);

        return product;
    }

    private MenuProduct createMenuProduct(int quantity, Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());

        return menuProduct;
    }

    private Menu createMenu(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProduct) {
        return createMenu(null, price, menuGroupId, menuProduct);
    }

    private Menu createMenu(String name, BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProduct) {
        return createMenu(null, name, price, menuGroupId, menuProduct);
    }

    private Menu createMenu(UUID uuid, String name, BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProduct) {
        Menu menu = new Menu();
        menu.setId(uuid);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProduct);
        menu.setDisplayed(false);
        return menu;
    }

    private MenuGroup createMenuGroup(UUID menuGroupId, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName(name);
        return menuGroup;
    }
}