package kitchenpos;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuServiceTest {

    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
    private final ProductRepository productRepository = new FakeProductRepository();
    private final ProfanityClient purgomalumClient = new FakeProfanityClient();


    private MenuService menuService;
    private Product savedProduct;
    private Product savedProduct2;
    private MenuProduct menuProduct;
    private MenuProduct menuProduct2;
    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        savedProduct = createProduct("후라이드치킨", 17_000);
        savedProduct2 = createProduct("양념치킨", 18_000);
        productRepository.save(savedProduct);
        productRepository.save(savedProduct2);

        menuProduct = TestFixtures.createMenuProductRequest(savedProduct, 1);
        menuProduct2 = TestFixtures.createMenuProductRequest(savedProduct2, 1);

        menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴그룹1");
        menuGroupRepository.save(menuGroup);

        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }


    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        // given
        Menu menu = createMenuRequest("후라이드양념세트", 30_000);

        addMenuGroupId(menu);
        addMenuProducts(menu);


        // when
        Menu result = menuService.create(menu);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(30_000)),
                () -> assertThat(result.getName()).isEqualTo("후라이드양념세트"),
                () -> assertThat(result.getMenuProducts()).isNotEmpty()
        );
    }


    @DisplayName("메뉴의 이름은 비속어가 포함될 수 없다.")
    @Test
    void menuNameBadWord() {
        // given
        Menu menu = createMenuRequest("욕설", 30_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);

        // when - then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);

    }


    @DisplayName("메뉴의 가격은 0 원 이상이어야 한다.")
    @Test
    void MenuPriceNegative() {
        // given
        Menu menu = createMenuRequest("후라이드양념세트", -100);
        addMenuGroupId(menu);
        addMenuProducts(menu);

        // when - thmenu.getId(),31_000en
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 그룹은 먼저 등록되어 있어야 한다.")
    @Test
    void menuGroupNotExists() {
        // given
        Menu menu = createMenuRequest("욕설", 30_000);
        addMenuProducts(menu);

        // when - then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);

    }


    @DisplayName("메뉴의 가격은 메뉴의 속하는 상품 가격의 합보다 크지 않아야 한다.")
    @Test
    void MenuSum() {
        // given
        Menu menu = createMenuRequest("후라이드양념세트", 36_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);

        // when - given
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("메뉴의 가격을 수정할 수 있다.")
    void update() {
        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 30_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);
        menuRepository.save(menu);

        Menu menuRequest = new Menu();
        menuRequest.setPrice(BigDecimal.valueOf(10_000));


        // when
        Menu result = menuService.changePrice(menu.getId(), menuRequest);

        // then
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(10_000));
    }

    @DisplayName("메뉴의 가격은 0원 이상이여야 하고 비어있지 않아야 수정 가능하다.")
    @NullSource
    @ValueSource(strings = {"-100", "-10000"})
    @ParameterizedTest
    void menuChangePriceNegative(Integer price) {
        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 30_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);
        menuRepository.save(menu);

        Menu menuRequest = new Menu();
        if (price != null) {
            menuRequest.setPrice(BigDecimal.valueOf(price));
        }

        // when - then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menuRequest))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴는 이미 등록되어 있어야 수정 가능하다.")
    @Test
    void menuNotSaved() {
        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 30_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);

        Menu menuRequest = new Menu();
        menuRequest.setPrice(BigDecimal.valueOf(10_000));

        // when - then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menuRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴에 속하는 상품 가격의 합보다 크지 않아야 수정 가능하다.")
    @Test
    void menuSum() {
        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 30_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);
        menuRepository.save(menu);

        Menu menuRequest = new Menu();
        menuRequest.setPrice(BigDecimal.valueOf(35_000));


        // when - then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menuRequest))
                .isInstanceOf(IllegalArgumentException.class);

    }


    @DisplayName("메뉴가 표시되어 보여진다.")
    @Test
    void menuDisplayed() {
        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 10_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);
        menuRepository.save(menu);

        // when
        Menu result = menuService.display(menu.getId());

        // then
        assertThat(result.isDisplayed()).isTrue();

    }

    @DisplayName("메뉴가 이미 등록되어 있어야 표시 가능하다.")
    @Test
    void menuNotRegistered() {
        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 10_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);

        // when - then
        assertThatThrownBy(() -> menuService.display(menu.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("메뉴의 가격은 메뉴에 속하는 상품 가격의 합보다 크지 않아야 표시 가능하다.")
    @Test
    void menuSumProductSum() {

        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 35_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);
        menuRepository.save(menu);

        // when - then
        assertThatThrownBy(() -> menuService.display(menu.getId()))
                .isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("메뉴가 표시되지 않으며 숨겨진다.")
    @Test
    void menuNotDisplayed() {
        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 10_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);
        menuRepository.save(menu);

        // when
        Menu result = menuService.hide(menu.getId());

        // then
        assertThat(result.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴가 이미 등록되어 있어야 숨길 수 있다.")
    @Test
    void menuNotRegisteredNotHide() {
        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 10_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);

        // when - then
        assertThatThrownBy(() -> menuService.hide(menu.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("메뉴의 목록을 조회할 수 있다.")
    @Test
    void test() {
        // given
        Menu menu = TestFixtures.createMenu("후라이드양념세트", 10_000);
        addMenuGroupId(menu);
        addMenuProducts(menu);
        menuRepository.save(menu);

        // when
        List<Menu> result = menuService.findAll();

        // then
        assertThat(result.size()).isEqualTo(1);
    }


    private void addMenuProducts(Menu menu) {
        menu.setMenuProducts(Arrays.asList(menuProduct, menuProduct2));
    }

    private void addMenuGroupId(Menu menu) {
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
    }
}
