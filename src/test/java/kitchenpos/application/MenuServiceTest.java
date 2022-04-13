package kitchenpos.application;

import static kitchenpos.application.MenuProductFixture.맛초킹_1개;
import static kitchenpos.application.MenuProductFixture.콜라_1개;
import static kitchenpos.application.MenuProductFixture.콜라_수량_오류;
import static kitchenpos.application.ProductFixture.맛초킹;
import static kitchenpos.application.ProductFixture.콜라;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.exception.MenuMarginException;
import kitchenpos.domain.exception.MenuPriceException;
import kitchenpos.domain.exception.MenuProductException;
import kitchenpos.domain.exception.MenuProductNotExistException;
import kitchenpos.domain.exception.MenuProductQuantityException;
import kitchenpos.infra.ProfanityClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("메뉴 관리")
class MenuServiceTest {

    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final ProfanityClient profanityClient = new FakeProfanityClient();

    private MenuService menuService;
    private MenuGroup 세트메뉴;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
        productRepository.save(맛초킹);
        productRepository.save(콜라);
        세트메뉴 = menuGroupRepository.save(MenuGroupFixture.세트메뉴);
    }

    @DisplayName("메뉴의 가격은 0원 이상이어야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    @NullSource
    void createPriceUnderZeroException(BigDecimal price) {
        //given
        Menu 잘못된_가격의_메뉴 = 메뉴생성()
            .withPrice(price)
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(잘못된_가격의_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(MenuPriceException.class);
    }

    @DisplayName("메뉴는 메뉴 그룹에 속해야 한다.")
    @Test
    void createMenuGroupNotExistException() {
        //given
        Menu 메뉴_그룹에_속하지_않은_메뉴 = 메뉴생성()
            .withMenuGroupId(null)
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(메뉴_그룹에_속하지_않은_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴는 1개 이상의 상품을 포함해야 한다.")
    @Test
    void createMenuProductsNotExistException() {
        //given
        Menu 상품_없는_메뉴 = 메뉴생성()
            .withMenuProducts(Collections.emptyList())
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(상품_없는_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(MenuProductNotExistException.class);
    }

    @DisplayName("중복된 메뉴상품을 가질 수 없다.")
    @Test
    void createMenuProductsMismatchException() {
        //given
        Menu 중복된_상품을_포함하는_메뉴 = 메뉴생성()
            .withMenuProducts(맛초킹_1개, 맛초킹_1개, 콜라_1개)
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(중복된_상품을_포함하는_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(MenuProductException.class);
    }

    @DisplayName("메뉴는 수량이 부족한 메뉴상품을 포함할 수 없다.")
    @Test
    void createMenuProductsLackQuantityException() {
        //given
        Menu 메뉴상품_수량_오류_메뉴 = 메뉴생성()
            .withMenuProducts(맛초킹_1개, 콜라_수량_오류)
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(메뉴상품_수량_오류_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(MenuProductQuantityException.class);
    }

    @DisplayName("메뉴의 가격은 상품들의 합산 가격과 같거나 작아야 한다.")
    @Test
    void createPriceException() {
        //given
        Menu 비싼_메뉴 = 메뉴생성()
            .withPrice(15_000L)
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(비싼_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(MenuMarginException.class);
    }

    @DisplayName("메뉴 이름에는 비속어가 포함될 수 없다.")
    @Test
    void createNameException() {
        //given
        Menu 비속어_메뉴 = 메뉴생성()
            .withName("비속어")
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(비속어_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성")
    @Test
    void create() {
        //given
        Menu 맛초킹_세트 = 메뉴생성().build();

        //when
        Menu menu = menuService.create(맛초킹_세트);

        //then
        assertAll(
            () -> assertThat(menu.getName()).isEqualTo(맛초킹_세트.getName()),
            () -> assertThat(menu.getMenuProducts()).hasSize(2),
            () -> assertThat(menu.getPrice()).isEqualTo(맛초킹_세트.getPrice()),
            () -> assertThat(menu.isDisplayed()).isEqualTo(맛초킹_세트.isDisplayed())
        );
    }

    @DisplayName("가격을 0원 미만으로 변경할 수 없다.")
    @ParameterizedTest(name = "변경 가격: [{arguments}]")
    @ValueSource(strings = {"-1"})
    @NullSource
    void changePriceException(BigDecimal price) {
        //given
        Menu 맛초킹_세트 = 맛초킹_세트_생성();

        Menu 가격_변경 = 가격_변경(price);

        //when
        ThrowingCallable actual = () -> menuService.changePrice(맛초킹_세트.getId(), 가격_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(MenuPriceException.class);
    }

    @DisplayName("상품들의 총 가격보다 높은 가격으로 변경할 수 없다.")
    @Test
    void changePriceGreaterThanProductsPricesException() {
        //given
        Menu 맛초킹_세트 = 맛초킹_세트_생성();

        BigDecimal 인상된_가격 = 맛초킹_세트.getPrice().add(BigDecimal.valueOf(10_000L));
        Menu 가격_변경 = 가격_변경(인상된_가격);

        //when
        ThrowingCallable actual = () -> menuService.changePrice(맛초킹_세트.getId(), 가격_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(MenuMarginException.class);
    }

    @DisplayName("가격 변경")
    @Test
    void changePrice() {
        //given
        Menu 맛초킹_세트 = 맛초킹_세트_생성();

        BigDecimal 인하된_가격 = 맛초킹_세트.getPrice().subtract(BigDecimal.valueOf(10_000L));
        Menu 가격_인하 = 가격_변경(인하된_가격);

        //when
        Menu menu = menuService.changePrice(맛초킹_세트.getId(), 가격_인하);
        BigDecimal actual = menu.getPrice();

        //then
        assertThat(actual).isEqualTo(인하된_가격);
    }

    @DisplayName("등록되지 않은 메뉴는 진열할 수 없다.")
    @Test
    void displayNotExistException() {
        //given
        Menu 없는_메뉴 = 등록_되지_않은_메뉴();

        //when
        ThrowingCallable actual = () -> menuService.display(없는_메뉴.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("상품들의 총 가격보다 높은 메뉴는 진열할 수 없다.")
    @Test
    void displayException() {
        //given
        Menu 마진_대박_맛초킹_세트 = 맛초킹_세트_생성(50_000);

        //when
        ThrowingCallable actual = () -> menuService.display(마진_대박_맛초킹_세트.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(MenuMarginException.class);
    }

    @DisplayName("메뉴를 진열한다.")
    @Test
    void display() {
        //given
        Menu 맛초킹_세트 = 맛초킹_세트_생성();

        //when
        Menu menu = menuService.display(맛초킹_세트.getId());

        //then
        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("등록되지 않은 메뉴는 진열에서 제외할 수 없다.")
    @Test
    void hideException() {
        //given
        Menu 없는_메뉴 = 등록_되지_않은_메뉴();

        //when
        ThrowingCallable actual = () -> menuService.hide(없는_메뉴.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴를 숨긴다.")
    @Test
    void hide() {
        //given
        Menu 맛초킹_세트 = 맛초킹_세트_생성();

        //when
        Menu actual = menuService.hide(맛초킹_세트.getId());

        //then
        assertThat(actual.isDisplayed()).isFalse();
    }

    @DisplayName("모든 메뉴 조회")
    @Test
    void findAll() {
        //given
        menuRepository.save(메뉴생성().build());
        menuRepository.save(메뉴생성().build());

        //when
        List<Menu> actual = menuService.findAll();

        //then
        assertThat(actual).hasSize(2);
    }

    private Menu 등록_되지_않은_메뉴() {
        return new MenuBuilder().build();
    }

    private Menu 맛초킹_세트_생성() {
        return 맛초킹_세트_생성(11_000);
    }

    private Menu 맛초킹_세트_생성(int price) {
        Menu 맛초킹_세트 = 메뉴생성()
            .withPrice(price)
            .build();
        return menuRepository.save(맛초킹_세트);
    }

    private Menu 가격_변경(BigDecimal price) {
        return new MenuBuilder()
            .withPrice(price)
            .build();
    }

    private MenuBuilder 메뉴생성() {
        return new MenuBuilder()
            .withMenuGroup(세트메뉴)
            .withMenuGroupId(세트메뉴.getId())
            .withName("맛초킹 세트")
            .withDisplayed(true)
            .withMenuProducts(Arrays.asList(맛초킹_1개, 콜라_1개))
            .withPrice(BigDecimal.valueOf(11_000L));
    }
}
