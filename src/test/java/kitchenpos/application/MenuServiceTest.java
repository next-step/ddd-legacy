package kitchenpos.application;

import static kitchenpos.application.fixture.MenuFixture.EMPTY_MENUPRODUCTS_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.EXPENSIVE_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.HIDED_MENU;
import static kitchenpos.application.fixture.MenuFixture.MENU1;
import static kitchenpos.application.fixture.MenuFixture.MENU1_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.MENU1_REQUEST_WRONG_PRODUCTS;
import static kitchenpos.application.fixture.MenuFixture.MENU2;
import static kitchenpos.application.fixture.MenuFixture.MENUS;
import static kitchenpos.application.fixture.MenuFixture.MENU_WITH_NAME_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.MENU_WITH_PRICE_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.PRICE_NEGATIVE_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.PRICE_NULL_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.QUANTITY_NAGATIVE_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP1;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT1;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MenuServiceTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;

    private final MenuRepository menuRepository = new InmemoryMenuRepository();
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private final ProductRepository productRepository = new InmemoryProductRepository();
    private final PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("create - 메뉴를 추가할 수 있다")
    @Test
    void createOK() {
        //given
        final MenuGroup menuGroup = menuGroupRepository.save(MENU_GROUP1());

        productRepository.save(PRODUCT1());
        productRepository.save(PRODUCT2());

        final Menu menuRequest = MENU1_REQUEST(menuGroup.getId());

        //when
        final Menu sut = menuService.create(menuRequest);

        //then
        assertAll(
            () -> assertThat(sut.getId()).isNotNull(),
            () -> assertThat(sut.getPrice()).isEqualTo(MENU1().getPrice()),
            () -> assertThat(sut.getName()).isEqualTo(MENU1().getName())
        );
    }

    @DisplayName("create - 메뉴 그룹이 존재하지 않으면 예외가 발생한다")
    @Test
    void menuGroupNotExist() {
        //given
        final Menu menuRequest = MENU1_REQUEST();

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.create(menuRequest));
    }

    @DisplayName("create - 가격이 없으면 예외가 발생한다")
    @Test
    void noPrice() {
        //given
        final Menu menuRequest = PRICE_NULL_MENU_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menuRequest));
    }

    @DisplayName("create - 가격이 음수이라면 예외가 발생한다")
    @Test
    void negativePrice() {
        //given
        final Menu menuRequest = PRICE_NEGATIVE_MENU_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menuRequest));
    }

    @DisplayName("create - 메뉴상품을 요청하지 않으면 예외가 발생한다")
    @Test
    void noMenuProduct() {
        //given
        final MenuGroup menuGroup = menuGroupRepository.save(MENU_GROUP1());

        final Menu menuRequest = EMPTY_MENUPRODUCTS_MENU_REQUEST(menuGroup.getId());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menuRequest));
    }

    @DisplayName("create - 메뉴 상품의 상품들 중 하나라도 존재하지 않으면 예외가 발생한다")
    @Test
    void productNotExist() {
        //given
        final MenuGroup menuGroup = menuGroupRepository.save(MENU_GROUP1());

        productRepository.save(PRODUCT1());
        productRepository.save(PRODUCT2());

        final Menu menuRequest = MENU1_REQUEST_WRONG_PRODUCTS(menuGroup.getId());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menuRequest));
    }

    @DisplayName("create - 메뉴 상품의 상품수량이 하나라도 음수인 것이 있으면 예외가 발생한다")
    @Test
    void productsNegativeQuentity() {
        //given
        final MenuGroup menuGroup = menuGroupRepository.save(MENU_GROUP1());

        productRepository.save(PRODUCT1());
        productRepository.save(PRODUCT2());

        final Menu menuRequest = QUANTITY_NAGATIVE_MENU_REQUEST(menuGroup.getId());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menuRequest));
    }

    @DisplayName("create - 메뉴 가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸다면 예외가 발생한다")
    @Test
    void menuValidPrice() {
        //given
        final MenuGroup menuGroup = menuGroupRepository.save(MENU_GROUP1());

        productRepository.save(PRODUCT1());
        productRepository.save(PRODUCT2());

        final Menu menuRequest = EXPENSIVE_MENU_REQUEST(menuGroup.getId());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menuRequest));
    }

    @DisplayName("create - 메뉴 이름이 한글자 미만이라면 예외가 발생한다")
    @ParameterizedTest
    @NullAndEmptySource
    void nullAndEmpty(final String name) {
        //given
        final MenuGroup menuGroup = menuGroupRepository.save(MENU_GROUP1());

        productRepository.save(PRODUCT1());
        productRepository.save(PRODUCT2());

        final Menu menuRequest = MENU_WITH_NAME_REQUEST(name, menuGroup.getId());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menuRequest));
    }

    @DisplayName("create - 메뉴 이름은 중복될 수 있다")
    @Test
    void duplicateName() {
        //given
        final MenuGroup menuGroup = menuGroupRepository.save(MENU_GROUP1());

        productRepository.save(PRODUCT1());
        productRepository.save(PRODUCT2());

        final Menu menuRequest1 = MENU1_REQUEST(menuGroup.getId());
        final Menu menuRequest2 = MENU1_REQUEST(menuGroup.getId());

        //when
        final Menu sut1 = menuService.create(menuRequest1);
        final Menu sut2 = menuService.create(menuRequest2);

        //then
        assertThat(sut1.getName()).isEqualTo(sut2.getName());
    }

    @DisplayName("create - 메뉴 이름에 비속어가 포함되어 있다면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "bitch", "Damn"})
    void profanityName(final String profanityName) {
        //given
        final MenuGroup menuGroup = menuGroupRepository.save(MENU_GROUP1());

        productRepository.save(PRODUCT1());
        productRepository.save(PRODUCT2());

        final Menu menuRequest = MENU_WITH_NAME_REQUEST(profanityName, menuGroup.getId());

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menuRequest));
    }

    @DisplayName("changePrice - 메뉴의 가격을 수정할 수 있다")
    @ParameterizedTest()
    @ValueSource(longs = {0L, 1L, 10000L, 20000L})
    void change(final long price) {
        //given
        final Menu menu = MENU1();

        menuRepository.save(menu);

        final Menu menuRequest = MENU_WITH_PRICE_REQUEST(price);

        //when
        final Menu sut = menuService.changePrice(menu.getId(), menuRequest);

        //then
        assertAll(
            () -> assertThat(sut.getId()).isNotNull(),
            () -> assertThat(sut.getPrice()).isEqualTo(BigDecimal.valueOf(price))
        );
    }

    @DisplayName("changePrice - 메뉴 가격이 없으면 예외가 발생한다")
    @Test
    void changeWithNoPrice() {
        //given
        final Menu menu = PRICE_NULL_MENU_REQUEST();

        menuRepository.save(menu);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("changePrice - 메뉴 가격이 음수라면 예외가 발생한다")
    @Test
    void changeWithNegativePrice() {
        //given
        final Menu menu = PRICE_NEGATIVE_MENU_REQUEST();

        menuRepository.save(menu);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("changePrice - 메뉴 가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸다면 예외가 발생한다")
    @Test
    void changeValidPrice() {
        //given
        final Menu menu = EXPENSIVE_MENU_REQUEST();

        menuRepository.save(menu);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("display - 메뉴가 노출되도록 변경할 수 있다")
    @Test
    void display() {
        //given
        final Menu menu = HIDED_MENU();

        menuRepository.save(menu);

        //when
        final Menu sut = menuService.display(menu.getId());

        //then
        assertThat(sut.isDisplayed()).isTrue();
    }

    @DisplayName("display - 메뉴가 존재하지 않으면 예외가 발생한다")
    @Test
    void displayNotExistMenu() {
        //given
        final Menu menu = MENU1();

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.display(menu.getId()));
    }

    @DisplayName("display - 메뉴 가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸다면 예외가 발생한다")
    @Test
    void displayMenuPrice() {
        //given
        final Menu menu = EXPENSIVE_MENU_REQUEST();

        menuRepository.save(menu);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.display(menu.getId()));
    }

    @DisplayName("hide - 메뉴가 노출되지 않도록 변경할 수 있다")
    @Test
    void hide() {
        //given
        final Menu menu = MENU1();

        menuRepository.save(menu);

        //when
        final Menu sut = menuService.hide(menu.getId());

        //then
        assertThat(sut.isDisplayed()).isFalse();
    }

    @DisplayName("hide - 메뉴가 존재하지 않으면 예외가 발생한다")
    @Test
    void hideNotExistMenu() {
        //given
        final Menu menu = MENU1();

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.hide(menu.getId()));
    }

    @DisplayName("findAll - 메뉴리스트를 조회할 수 있다")
    @Test
    void findAll() {
        //given
        final Menu menu1 = MENU1();
        final Menu menu2 = MENU2();

        menuRepository.save(menu1);
        menuRepository.save(menu2);

        //when
        final List<Menu> menus = menuService.findAll();

        //then
        assertAll(
            () -> assertThat(menus).hasSize(MENUS().size()),
            () -> assertThat(menus.get(ZERO)
                .getId()).isEqualTo(menu1.getId()),
            () -> assertThat(menus.get(ONE)
                .getId()).isEqualTo(menu2.getId())
        );
    }

}
