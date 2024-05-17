package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.List;
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
        Menu request = new Menu();
        request.setPrice(price);

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격이 음수라면 메뉴 생성 시 IllegalArgument가 발생한다.")
    void create_fail_for_negative_price() {
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(-1));

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴 상품이 null이거나 비어있다면 메뉴 생성 시 IllegalArgument가 발생한다.")
    void create_fail_for_null_or_empty_menu_product_requests(List<MenuProduct> menuProductRequests) {
        Menu request = new Menu();
        request.setMenuProducts(menuProductRequests);

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴상품의 개수와 실제 대응되는 상품의 개수가 다를 경우 IllegalArgument가 발생한다.")
    void create_fail_for_not_same_product_size_and_menuproductrequest_size() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);;

        Product product = new Product("후라이드치킨", BigDecimal.valueOf(16_000L));
        Product savedProduct = productRepository.save(product);

        Menu request = new Menu();
        request.setMenuGroupId(savedMenuGroup.getId());
        request.setPrice(BigDecimal.valueOf(16_000L));
        request.setName("치킨 세트A");
        request.setMenuProducts(List.of(
            new MenuProduct(savedProduct, 1L),
            new MenuProduct(new Product(savedProduct.getId(), "양념치킨", BigDecimal.valueOf(16_000L)), 1L)
        ));

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격이 메뉴상품의 가격 총합보다 같거나 크다면 메뉴 생성 시 IllegalArgumentException이 발생한다.")
    void create_fail_for_higher_menu_price_than_menu_product_price_sum() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);;

        Product product = new Product("후라이드치킨", BigDecimal.valueOf(16_000L));
        Product savedProduct = productRepository.save(product);

        Menu request = new Menu();
        request.setMenuGroupId(savedMenuGroup.getId());
        request.setPrice(BigDecimal.valueOf(17_000L));
        request.setName("치킨 세트A");
        request.setMenuProducts(List.of(new MenuProduct(savedProduct, 1L)));

        assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("메뉴 이름이 null이라면 IllegalArgument가 발생한다.")
    void create_fail_for_null_name(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);;

        Product product = new Product("후라이드치킨", BigDecimal.valueOf(16_000L));
        Product savedProduct = productRepository.save(product);

        Menu request = new Menu();
        request.setMenuGroupId(savedMenuGroup.getId());
        request.setPrice(BigDecimal.valueOf(16_000L));
        request.setName(name);
        request.setMenuProducts(List.of(new MenuProduct(savedProduct, 1L)));

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 이름에 욕설이 포함되어 있다면 IllegalArgument가 발생한다.")
    void create_fail_for_profanity_name() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product = new Product("후라이드치킨", BigDecimal.valueOf(16_000L));
        Product savedProduct = productRepository.save(product);

        Menu request = new Menu();
        request.setMenuGroupId(savedMenuGroup.getId());
        request.setPrice(BigDecimal.valueOf(16_000L));
        request.setName(FakePurgomalumClient.PROFANITY);
        request.setMenuProducts(List.of(new MenuProduct(savedProduct, 1L)));

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 생성한다.")
    void create_success() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product = new Product("후라이드치킨", BigDecimal.valueOf(16_000L));
        Product savedProduct = productRepository.save(product);

        Menu request = new Menu();
        request.setMenuGroupId(savedMenuGroup.getId());
        request.setPrice(BigDecimal.valueOf(16_000L));
        request.setName("메뉴 이름");
        request.setMenuProducts(List.of(new MenuProduct(savedProduct, 1L)));

        Menu menu = menuService.create(request);

        assertThat(menu.getId()).isNotNull();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("가격이 null이라면 메뉴 가격 변경 시 IllegalArgument가 발생한다.")
    void changePrice_fail_for_null_price(BigDecimal price) {
        Menu request = new Menu();
        request.setPrice(price);

        assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격이 음수라면 메뉴 가격 변경 시 IllegalArgument가 발생한다.")
    void changePrice_fail_for_negative_price() {
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(-1));

        assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격이 메뉴상품의 가격 총합보다 같거나 크다면 가격 변경 시 IllegalArgumentException이 발생한다.")
    void changePrice_fail_for_higher_menu_price_than_menu_product_price_sum() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);;

        Product product = new Product("후라이드치킨", BigDecimal.valueOf(16_000L));
        Product savedProduct = productRepository.save(product);

        Menu menuRequest = new Menu();
        menuRequest.setMenuGroupId(savedMenuGroup.getId());
        menuRequest.setPrice(BigDecimal.valueOf(16_000L));
        menuRequest.setName("치킨 세트A");
        menuRequest.setMenuProducts(List.of(new MenuProduct(savedProduct, 1L)));

        Menu savedMenu = menuService.create(menuRequest);

        Menu changePriceRequest = new Menu();
        changePriceRequest.setPrice(BigDecimal.valueOf(17_000L));

        assertThatThrownBy(() -> menuService.changePrice(savedMenu.getId(), changePriceRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격을 변경한다.")
    void changePrice_success() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);;

        Product product = new Product("후라이드치킨", BigDecimal.valueOf(16_000L));
        Product savedProduct = productRepository.save(product);

        Menu menuRequest = new Menu();
        menuRequest.setMenuGroupId(savedMenuGroup.getId());
        menuRequest.setPrice(BigDecimal.valueOf(16_000L));
        menuRequest.setName("치킨 세트A");
        menuRequest.setMenuProducts(List.of(new MenuProduct(savedProduct, 1L)));

        Menu savedMenu = menuService.create(menuRequest);

        Menu changePriceRequest = new Menu();
        changePriceRequest.setPrice(BigDecimal.valueOf(16_000L));

        menuService.changePrice(savedMenu.getId(), changePriceRequest);

        Menu changedMenu = menuRepository.findById(savedMenu.getId()).orElseThrow();
        assertThat(changedMenu.getPrice()).isEqualTo(BigDecimal.valueOf(changePriceRequest.getPrice().longValue()));
    }
}