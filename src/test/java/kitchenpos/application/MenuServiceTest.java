package kitchenpos.application;

import static kitchenpos.application.fixture.MenuFixture.EMPTY_MENUPRODUCTS_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.EXPENSIVE_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuFixture.HIDED_MENU;
import static kitchenpos.application.fixture.MenuFixture.MENU1;
import static kitchenpos.application.fixture.MenuFixture.MENU1_REQUEST;
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
import static kitchenpos.application.fixture.ProductFixture.PRODUCTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import kitchenpos.domain.Menu;
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
import org.mockito.Mock;

class MenuServiceTest extends MockTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("create - 메뉴를 추가할 수 있다")
    @Test
    void createOK() {
        //given
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(MENU_GROUP1()));
        given(productRepository.findAllById(any())).willReturn(PRODUCTS());
        given(productRepository.findById(any())).willReturn(Optional.of(PRODUCT1()))
            .willReturn(Optional.of(PRODUCT2()));
        given(menuRepository.save(any())).willReturn(MENU1());

        //when
        final Menu sut = menuService.create(MENU1_REQUEST());

        //then
        assertAll(
            () -> assertThat(sut.getId()).isEqualTo(MENU1().getId()),
            () -> assertThat(sut.getPrice()).isEqualTo(MENU1().getPrice()),
            () -> assertThat(sut.getName()).isEqualTo(MENU1().getName())
        );
    }

    @DisplayName("create - 메뉴 그룹이 존재하지 않으면 예외가 발생한다")
    @Test
    void menuGroupNotExist() {
        //given
        final Menu menu = MENU1_REQUEST();

        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 가격이 없으면 예외가 발생한다")
    @Test
    void noPrice() {
        //given
        final Menu menu = PRICE_NULL_MENU_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 가격이 음수이라면 예외가 발생한다")
    @Test
    void negativePrice() {
        //given
        final Menu menu = PRICE_NEGATIVE_MENU_REQUEST();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴상품을 요청하지 않으면 예외가 발생한다")
    @Test
    void noMenuProduct() {
        //given
        final Menu menu = EMPTY_MENUPRODUCTS_MENU_REQUEST();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(MENU_GROUP1()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 상품의 상품들 중 하나라도 존재하지 않으면 예외가 발생한다")
    @Test
    void productNotExist() {
        //given
        final Menu menu = MENU1_REQUEST();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(MENU_GROUP1()));
        given(productRepository.findAllById(any())).willReturn(PRODUCTS());
        given(productRepository.findById(any())).willReturn(Optional.of(PRODUCT1()))
            .willReturn(Optional.empty());

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 상품의 상품수량이 하나라도 음수인 것이 있으면 예외가 발생한다")
    @Test
    void productsNegativeQuentity() {
        //given
        final Menu menu = QUANTITY_NAGATIVE_MENU_REQUEST();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(MENU_GROUP1()));
        given(productRepository.findAllById(any())).willReturn(PRODUCTS());
        given(productRepository.findById(any())).willReturn(Optional.of(PRODUCT1()))
            .willReturn(Optional.of(PRODUCT2()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸다면 예외가 발생한다")
    @Test
    void menuValidPrice() {
        //given
        final Menu menu = EXPENSIVE_MENU_REQUEST();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(MENU_GROUP1()));
        given(productRepository.findAllById(any())).willReturn(PRODUCTS());
        given(productRepository.findById(any())).willReturn(Optional.of(PRODUCT1()))
            .willReturn(Optional.of(PRODUCT2()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 이름이 한글자 미만이라면 예외가 발생한다")
    @ParameterizedTest
    @NullAndEmptySource
    void nullAndEmpty(final String name) {
        //given
        final Menu menu = MENU_WITH_NAME_REQUEST(name);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(MENU_GROUP1()));
        given(productRepository.findAllById(any())).willReturn(PRODUCTS());
        given(productRepository.findById(any())).willReturn(Optional.of(PRODUCT1()))
            .willReturn(Optional.of(PRODUCT2()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 이름은 중복될 수 있다")
    @Test
    void duplicateName() {
        //given
        final Menu menu1 = MENU1();
        final Menu menu2 = MENU1();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(MENU_GROUP1()));
        given(productRepository.findAllById(any())).willReturn(PRODUCTS());
        given(productRepository.findById(any())).willReturn(Optional.of(PRODUCT1()))
            .willReturn(Optional.of(PRODUCT2()));
        given(menuRepository.save(any())).willReturn(menu1, menu2);

        //when
        final Menu sut1 = menuService.create(menu1);
        final Menu sut2 = menuService.create(menu2);

        //then
        assertThat(sut1.getName()).isEqualTo(sut2.getName());
    }

    @DisplayName("create - 메뉴 이름에 비속어가 포함되어 있다면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "bitch", "Damn"})
    void profanityName(final String profanityName) {
        //given
        final Menu menu = MENU_WITH_NAME_REQUEST(profanityName);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(MENU_GROUP1()));
        given(productRepository.findAllById(any())).willReturn(PRODUCTS());
        given(productRepository.findById(any())).willReturn(Optional.of(PRODUCT1()))
            .willReturn(Optional.of(PRODUCT2()));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("changePrice - 메뉴의 가격을 수정할 수 있다")
    @ParameterizedTest()
    @ValueSource(longs = {0L, 1L, 10000L, 20000L})
    void change(final long price) {
        //given
        final Menu menu = MENU1();
        final Menu menuRequest = MENU_WITH_PRICE_REQUEST(price);

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

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

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("changePrice - 메뉴 가격이 음수라면 예외가 발생한다")
    @Test
    void changeWithNegativePrice() {
        //given
        final Menu menu = PRICE_NEGATIVE_MENU_REQUEST();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("changePrice - 메뉴 가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸다면 예외가 발생한다")
    @Test
    void changeValidPrice() {
        //given
        final Menu menu = EXPENSIVE_MENU_REQUEST();

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("display - 메뉴가 노출되도록 변경할 수 있다")
    @Test
    void display() {
        //given
        final Menu menu = HIDED_MENU();

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

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

        given(menuRepository.findById(any())).willReturn(Optional.empty());

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.display(menu.getId()));
    }

    @DisplayName("display - 메뉴 가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸다면 예외가 발생한다")
    @Test
    void displayMenuPrice() {
        //given
        final Menu menu = EXPENSIVE_MENU_REQUEST();

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.display(menu.getId()));
    }

    @DisplayName("hide - 메뉴가 노출되지 않도록 변경할 수 있다")
    @Test
    void hide() {
        //given
        final Menu menu = MENU1();

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

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

        given(menuRepository.findById(any())).willReturn(Optional.empty());

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.hide(menu.getId()));
    }

    @DisplayName("findAll - 메뉴리스트를 조회할 수 있다")
    @Test
    void findAll() {
        //given
        given(menuRepository.findAll()).willReturn(MENUS());

        //when
        final List<Menu> menus = menuService.findAll();

        //then
        assertAll(
            () -> assertThat(menus.size()).isEqualTo(MENUS().size()),
            () -> assertThat(menus.get(ZERO)
                .getId()).isEqualTo(MENU1().getId()),
            () -> assertThat(menus.get(ONE)
                .getId()).isEqualTo(MENU2().getId())
        );
    }

}
