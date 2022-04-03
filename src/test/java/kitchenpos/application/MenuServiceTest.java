package kitchenpos.application;

import static kitchenpos.application.MenuGroupFixture.세트메뉴;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
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

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
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
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 메뉴 그룹에 속해야 한다.")
    @Test
    void createMenuGroupNotExistException() {
        //given
        Menu 메뉴_그룹에_속하지_않은_메뉴 = 메뉴생성()
            .withMenuGroup(null)
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
        MenuGroup 세트_메뉴 = menuGroupRepository.save(세트메뉴);

        Menu 상품_없는_메뉴 = 메뉴생성()
            .withMenuGroup(세트_메뉴)
            .withMenuGroupId(세트_메뉴.getId())
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(상품_없는_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("중복된 메뉴상품을 가질 수 없다.")
    @Test
    void createMenuProductsMismatchException() {
        //given
        MenuGroup 세트_메뉴 = menuGroupRepository.save(세트메뉴);
        productRepository.save(맛초킹);
        productRepository.save(콜라);

        Menu 중복된_상품을_포함하는_메뉴 = 메뉴생성()
            .withMenuGroup(세트_메뉴)
            .withMenuGroupId(세트_메뉴.getId())
            .withMenuProducts(맛초킹_1개, 맛초킹_1개, 콜라_1개)
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(중복된_상품을_포함하는_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 수량이 부족한 메뉴상품을 포함할 수 없다.")
    @Test
    void createMenuProductsLackQuantityException() {
        //given
        MenuGroup 세트_메뉴 = menuGroupRepository.save(세트메뉴);
        productRepository.save(맛초킹);
        productRepository.save(콜라);

        Menu 메뉴상품_수량_오류_메뉴 = 메뉴생성()
            .withMenuGroup(세트_메뉴)
            .withMenuGroupId(세트_메뉴.getId())
            .withMenuProducts(맛초킹_1개, 콜라_수량_오류)
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(메뉴상품_수량_오류_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 상품들의 합산 가격과 같거나 작아야 한다.")
    @Test
    void createPriceException() {
        //given
        MenuGroup 세트_메뉴 = menuGroupRepository.save(세트메뉴);
        productRepository.save(맛초킹);
        productRepository.save(콜라);

        Menu 비싼_메뉴 = 메뉴생성()
            .withPrice(15_000L)
            .withMenuGroup(세트_메뉴)
            .withMenuGroupId(세트_메뉴.getId())
            .withMenuProducts(맛초킹_1개, 콜라_1개)
            .build();

        //when
        ThrowingCallable actual = () -> menuService.create(비싼_메뉴);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름에는 비속어가 포함될 수 없다.")
    @Test
    void createNameException() {
        //given
        MenuGroup 세트_메뉴 = menuGroupRepository.save(세트메뉴);
        productRepository.save(맛초킹);
        productRepository.save(콜라);

        Menu 비속어_메뉴 = 메뉴생성()
            .withName("비속어")
            .withMenuGroupId(세트_메뉴.getId())
            .withMenuProducts(맛초킹_1개, 콜라_1개)
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
        Menu 맛초킹_세트 = 맛초킹_세트_생성(11_000);

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
        Menu 포장_전용_메뉴 = menuRepository.save(메뉴생성().build());

        Menu 가격_변경 = 메뉴_생성(price);

        //when
        ThrowingCallable actual = () -> menuService.changePrice(포장_전용_메뉴.getId(), 가격_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품들의 총 가격보다 높은 가격으로 변경할 수 없다.")
    @Test
    void changePriceGreaterThanProductsPricesException() {
        //given
        Menu 맛초킹_세트 = 맛초킹_세트_생성(11_000);
        Menu 가격_변경 = 메뉴_생성(50_000);

        //when
        ThrowingCallable actual = () -> menuService.changePrice(맛초킹_세트.getId(), 가격_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격 변경")
    @Test
    void changePrice() {
        //given
        Menu 맛초킹_세트 = 맛초킹_세트_생성(11_000);
        Menu 가격_인하 = 메뉴_생성(10_500);

        //when
        Menu menu = menuService.changePrice(맛초킹_세트.getId(), 가격_인하);
        BigDecimal actual = menu.getPrice();

        //then
        assertThat(actual).isEqualTo(BigDecimal.valueOf(10_500L));
    }

    @DisplayName("등록되지 않은 메뉴는 진열할 수 없다.")
    @Test
    void displayNotExistException() {
        //given
        Menu 없는_메뉴 = new MenuBuilder().build();

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
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("진열")
    @Test
    void display() {
        //given
        Menu 맛초킹_세트 = 맛초킹_세트_생성(11_000);

        //when
        Menu menu = menuService.display(맛초킹_세트.getId());

        //then
        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("등록되지 않은 메뉴는 진열에서 제외할 수 없다.")
    @Test
    void hideException() {
        //given
        Menu 없는_메뉴 = new MenuBuilder().build();

        //when
        ThrowingCallable actual = () -> menuService.hide(없는_메뉴.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("진열 제외")
    @Test
    void hide() {
        //given
        Menu 맛초킹_세트 = 맛초킹_세트_생성(11_000);

        //when
        Menu menu = menuService.hide(맛초킹_세트.getId());

        //then
        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("모든 메뉴 조회")
    @Test
    void findALl() {
        //given
        menuRepository.save(메뉴생성().build());
        menuRepository.save(메뉴생성().build());

        //when
        List<Menu> actual = menuService.findAll();

        //then
        assertThat(actual).hasSize(2);
    }

    private Menu 맛초킹_세트_생성(int price) {
        MenuGroup 세트_메뉴 = menuGroupRepository.save(세트메뉴);
        Product 맛초킹 = productRepository.save(ProductFixture.맛초킹);
        Product 콜라 = productRepository.save(ProductFixture.콜라);

        MenuProduct 맛초킹1개 = 메뉴_상품_생성(맛초킹);
        MenuProduct 콜라1개 = 메뉴_상품_생성(콜라);

        Menu 맛초킹_세트 = 메뉴생성()
            .withMenuGroupId(세트_메뉴.getId())
            .withMenuGroup(세트메뉴)
            .withMenuProducts(Arrays.asList(맛초킹1개, 콜라1개))
            .withName("맛초킹 세트")
            .withPrice(price)
            .build();
        return menuRepository.save(맛초킹_세트);
    }

    private MenuProduct 메뉴_상품_생성(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(1L);
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    private Menu 메뉴_생성(int price) {
        return 메뉴_생성(BigDecimal.valueOf(price));
    }

    private Menu 메뉴_생성(BigDecimal price) {
        return new MenuBuilder()
            .withPrice(price)
            .build();
    }

    private MenuBuilder 메뉴생성() {
        return new MenuBuilder()
            .withMenuGroup(new MenuGroup())
            .withMenuGroupId(UUID.randomUUID())
            .withName("맛초킹 세트")
            .withDisplayed(true)
            .withMenuProducts(Arrays.asList(맛초킹_1개, 콜라_1개))
            .withPrice(BigDecimal.valueOf(11_000L));
    }
}
