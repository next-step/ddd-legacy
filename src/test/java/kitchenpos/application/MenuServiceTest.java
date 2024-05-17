package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuServiceTest {

    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private ProductRepository productRepository = new InMemoryProductRepository();
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("가격이 null이라면 메뉴 생성 시 IllegalArgument가 발생한다.")
    void create_fail_for_null_price(BigDecimal price) {
        Menu request = menuRequestBuilder()
                .withPrice(price)
                .build();

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격이 음수라면 메뉴 생성 시 IllegalArgument가 발생한다.")
    void create_fail_for_negative_price() {
        Menu request = menuRequestBuilder()
                .withPrice(BigDecimal.valueOf(-1L))
                .build();

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴 상품이 null이거나 비어있다면 메뉴 생성 시 IllegalArgument가 발생한다.")
    void create_fail_for_null_or_empty_menu_product_requests(List<MenuProduct> menuProductRequests) {
        Menu request = menuRequestBuilder()
                .withMenuProducts(menuProductRequests)
                .build();

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴상품의 개수와 실제 대응되는 상품의 개수가 다를 경우 IllegalArgument가 발생한다.")
    void create_fail_for_not_same_product_size_and_menuproductrequest_size() {
        Product savedProduct = createAndSaveProduct();
        List<MenuProduct> menuProducts = List.of(
                new MenuProduct(savedProduct, 1L),
                new MenuProduct(new Product(savedProduct.getId(), "상품 이름2", BigDecimal.valueOf(10_000L)), 1L)
        );

        Menu request = menuRequestBuilder()
                .withMenuProducts(menuProducts)
                .build();

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격이 메뉴상품의 가격 총합보다 크다면 메뉴 생성 시 IllegalArgumentException이 발생한다.")
    void create_fail_for_higher_menu_price_than_menu_product_price_sum() {
        Menu request = menuRequestBuilder()
                .withPrice(MenuRequestBuilder.DEFAULT_PRICE.add(BigDecimal.ONE))
                .build();

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("메뉴 이름이 null이라면 IllegalArgument가 발생한다.")
    void create_fail_for_null_name(String name) {
        Menu request = menuRequestBuilder()
                .withName(name)
                .build();

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 이름에 욕설이 포함되어 있다면 IllegalArgument가 발생한다.")
    void create_fail_for_profanity_name() {
        Menu request = menuRequestBuilder()
                .withName(FakePurgomalumClient.PROFANITY)
                .build();

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 생성한다.")
    void create_success() {
        Menu request = menuRequestBuilder().build();
        Menu menu = menuService.create(request);
        assertThat(menu.getId()).isNotNull();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("가격이 null이라면 메뉴 가격 변경 시 IllegalArgument가 발생한다.")
    void changePrice_fail_for_null_price(BigDecimal price) {
        Menu savedMenu = createAndSaveMenu();

        Menu request = menuRequestBuilder()
                .withPrice(price)
                .build();

        assertThatThrownBy(() -> menuService.changePrice(savedMenu.getId(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격이 음수라면 메뉴 가격 변경 시 IllegalArgument가 발생한다.")
    void changePrice_fail_for_negative_price() {
        Menu savedMenu = createAndSaveMenu();

        Menu request = menuRequestBuilder()
                .withPrice(BigDecimal.valueOf(-1L))
                .build();

        assertThatThrownBy(() -> menuService.changePrice(savedMenu.getId(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격이 메뉴상품의 가격 총합보다 크다면 가격 변경 시 IllegalArgumentException이 발생한다.")
    void changePrice_fail_for_higher_menu_price_than_menu_product_price_sum() {
        Menu savedMenu = createAndSaveMenu();

        Menu request = menuRequestBuilder()
                .withPrice(savedMenu.getPrice().add(BigDecimal.ONE))
                .build();

        assertThatThrownBy(() -> menuService.changePrice(savedMenu.getId(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격을 변경한다.")
    void changePrice_success() {
        Menu savedMenu = createAndSaveMenu();
        Menu request = menuRequestBuilder()
                .withPrice(savedMenu.getPrice().subtract(BigDecimal.ONE))
                .build();

        menuService.changePrice(savedMenu.getId(), request);

        Menu changedMenu = menuRepository.findById(savedMenu.getId()).orElseThrow();
        assertThat(changedMenu.getPrice()).isEqualTo(BigDecimal.valueOf(request.getPrice().longValue()));
    }

    @Test
    @DisplayName("메뉴가 존재하지 않으면 메뉴 표시 시 NoSuchElementException이 발생한다.")
    void display_fail_for_not_existing_menu() {
        assertThatThrownBy(() -> menuService.display(UUID.randomUUID()))
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴 가격이 메뉴상품 가격의 총합보다 크다면 메뉴 표시 시 IllegalStateException이 발생한다")
    void display_fail_for_higher_menu_price_than_menu_product_price_sum() {
        Menu savedMenu = createAndSaveMenu();
        savedMenu.setDisplayed(false);
        savedMenu.setPrice(savedMenu.getPrice().add(BigDecimal.ONE));

        assertThatThrownBy(() -> menuService.display(savedMenu.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("메뉴를 표시한다.")
    void display_success() {
        Menu savedMenu = createAndSaveMenu();
        savedMenu.setDisplayed(false);

        menuService.display(savedMenu.getId());

        Menu displayedMenu = menuRepository.findById(savedMenu.getId()).orElseThrow();
        assertThat(displayedMenu.isDisplayed()).isTrue();
    }

    private MenuRequestBuilder menuRequestBuilder() {
        MenuGroup savedMenuGroup = createAndSaveMenuGroup();
        Product savedProduct = createAndSaveProduct();
        return new MenuRequestBuilder(savedMenuGroup, List.of(new MenuProduct(savedProduct, 1L)));
    }

    private Product createAndSaveProduct() {
        Product product = new Product("상품 이름1", MenuRequestBuilder.DEFAULT_PRICE);
        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }

    private MenuGroup createAndSaveMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);
        return savedMenuGroup;
    }

    private Menu createAndSaveMenu() {
        Menu request = menuRequestBuilder().build();
        return menuRepository.save(request);
    }
}