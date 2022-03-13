package kitchenpos.application;

import static kitchenpos.application.MenuFixture.뿌링클_세트;
import static kitchenpos.application.MenuGroupFixture.세트메뉴;
import static kitchenpos.application.MenuProductFixture.맛초킹_1개;
import static kitchenpos.application.MenuProductFixture.콜라_1개;
import static kitchenpos.application.MenuProductFixture.콜라_수량_오류;
import static kitchenpos.application.ProductFixture.맛초킹;
import static kitchenpos.application.ProductFixture.뿌링클;
import static kitchenpos.application.ProductFixture.콜라;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴")
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock(lenient = true)
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;

    private Menu 맛초킹_세트;

    @BeforeEach
    void setUp() {
        맛초킹_세트 = new Menu();
        맛초킹_세트.setName("맛초킹 세트");
        맛초킹_세트.setMenuGroup(세트메뉴);
        맛초킹_세트.setMenuGroupId(세트메뉴.getId());
        맛초킹_세트.setMenuProducts(Arrays.asList(맛초킹_1개, 콜라_1개));
        맛초킹_세트.setPrice(BigDecimal.valueOf(11_000L));
        맛초킹_세트.setDisplayed(true);
    }

    @DisplayName("메뉴 생성 예외 - 메뉴 가격 0원 미만")
    @Test
    void createPriceUnderZeroException() {
        //given
        맛초킹_세트.setPrice(BigDecimal.valueOf(-1L));

        //when
        ThrowingCallable actual = () -> menuService.create(맛초킹_세트);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 예외 - 메뉴 그룹 없음")
    @Test
    void createMenuGroupNotExistException() {
        //given
        맛초킹_세트.setMenuGroup(null);

        //when
        ThrowingCallable actual = () -> menuService.create(맛초킹_세트);

        //then
        assertThatThrownBy(actual).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 생성 예외 - 메뉴 상품 없음")
    @Test
    void createMenuProductsNotExistException() {
        //given
        맛초킹_세트.setMenuProducts(null);

        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(세트메뉴));

        //when
        ThrowingCallable actual = () -> menuService.create(맛초킹_세트);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 예외 - 메뉴 상품 개수 미일치")
    @Test
    void createMenuProductsMismatchException() {
        //given
        맛초킹_세트.setMenuProducts(Arrays.asList(맛초킹_1개, 맛초킹_1개, 콜라_1개));

        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(세트메뉴));
        given(productRepository.findAllByIdIn(anyList())).willReturn(Arrays.asList(맛초킹, 콜라));

        //when
        ThrowingCallable actual = () -> menuService.create(맛초킹_세트);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 예외 - 메뉴 상품 수량 부족")
    @Test
    void createMenuProductsLackQuantityException() {
        맛초킹_세트.setMenuProducts(Arrays.asList(맛초킹_1개, 콜라_수량_오류));

        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(세트메뉴));
        given(productRepository.findAllByIdIn(anyList())).willReturn(Arrays.asList(맛초킹, 콜라));
        given(productRepository.findById(맛초킹.getId())).willReturn(Optional.of(맛초킹));

        //when
        ThrowingCallable actual = () -> menuService.create(맛초킹_세트);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 예외 - 상품들의 총 가격보다 높음")
    @Test
    void createPriceException() {
        //given
        맛초킹_세트.setPrice(BigDecimal.valueOf(15_000L));

        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(세트메뉴));
        given(productRepository.findAllByIdIn(anyList())).willReturn(Arrays.asList(맛초킹, 콜라));
        given(productRepository.findById(맛초킹.getId())).willReturn(Optional.of(맛초킹));
        given(productRepository.findById(콜라.getId())).willReturn(Optional.of(콜라));

        //when
        ThrowingCallable actual = () -> menuService.create(맛초킹_세트);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 예외 - 이름에 비속어 포함")
    @Test
    void createNameException() {
        //given
        맛초킹_세트.setName("비속어");

        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(세트메뉴));
        given(productRepository.findAllByIdIn(anyList())).willReturn(Arrays.asList(맛초킹, 콜라));
        given(productRepository.findById(맛초킹.getId())).willReturn(Optional.of(맛초킹));
        given(productRepository.findById(콜라.getId())).willReturn(Optional.of(콜라));
        given(purgomalumClient.containsProfanity("비속어")).willReturn(true);

        //when
        ThrowingCallable actual = () -> menuService.create(맛초킹_세트);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성")
    @Test
    void create() {
        //given
        given(menuGroupRepository.findById(any(UUID.class))).willReturn(Optional.of(세트메뉴));
        given(productRepository.findAllByIdIn(anyList())).willReturn(Arrays.asList(뿌링클, 콜라));
        given(productRepository.findById(뿌링클.getId())).willReturn(Optional.of(뿌링클));
        given(productRepository.findById(콜라.getId())).willReturn(Optional.of(콜라));
        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
        given(menuRepository.save(any(Menu.class))).willReturn(뿌링클_세트);

        //when
        Menu menu = menuService.create(뿌링클_세트);

        //then
        assertAll(
            () -> assertThat(menu.getName()).isEqualTo("뿌링클 세트"),
            () -> assertThat(menu.getMenuGroupId()).isEqualTo(세트메뉴.getId()),
            () -> assertThat(menu.getMenuProducts()).hasSize(2),
            () -> assertThat(menu.getPrice()).isEqualTo(뿌링클_세트.getPrice()),
            () -> assertThat(menu.isDisplayed()).isTrue()
        );
    }

    @DisplayName("가격 변경 예외 - 0원 미만")
    @ParameterizedTest(name = "변경 가격: [{arguments}]")
    @MethodSource("changePriceException")
    void changePriceException(BigDecimal price) {
        //given
        맛초킹_세트.setId(UUID.randomUUID());
        맛초킹_세트.setPrice(price);

        //when
        ThrowingCallable actual = () -> menuService.changePrice(맛초킹_세트.getId(), 맛초킹_세트);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> changePriceException() {
        return Stream.of(
            Arguments.of((BigDecimal) null),
            Arguments.of(BigDecimal.valueOf(-1))
        );
    }

    @DisplayName("가격 변경 예외 - 상품들의 총 가격보다 높음")
    @Test
    void changePriceGreaterThanProductsPricesException() {
        //given
        맛초킹_세트.setId(UUID.randomUUID());
        맛초킹_세트.setPrice(BigDecimal.valueOf(15_000L));

        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.of(맛초킹_세트));

        //when
        ThrowingCallable actual = () -> menuService.changePrice(맛초킹_세트.getId(), 맛초킹_세트);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격 변경")
    @Test
    void changePrice() {
        //given
        Menu 변경할_가격 = new Menu();
        변경할_가격.setPrice(BigDecimal.valueOf(10_500L));

        given(menuRepository.findById(any())).willReturn(Optional.of(뿌링클_세트));

        //when
        Menu menu = menuService.changePrice(뿌링클_세트.getId(), 변경할_가격);
        BigDecimal actual = menu.getPrice();

        //then
        assertThat(actual).isEqualTo(BigDecimal.valueOf(10_500L));
    }

    @DisplayName("진열 예외 - 상품들의 총 가격보다 높음")
    @Test
    void displayException() {
        //given
        맛초킹_세트.setPrice(BigDecimal.valueOf(15_000L));

        given(menuRepository.findById(any())).willReturn(Optional.of(맛초킹_세트));

        //when
        ThrowingCallable actual = () -> menuService.display(맛초킹_세트.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("진열")
    @Test
    void display() {
        //given
        맛초킹_세트.setPrice(BigDecimal.valueOf(11_000L));

        given(menuRepository.findById(any())).willReturn(Optional.of(맛초킹_세트));

        //when
        Menu menu = menuService.display(맛초킹_세트.getId());

        //then
        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("진열 제외 - 등록된 메뉴만 진열 제외 가능")
    @Test
    void hideException() {
        //given
        given(menuRepository.findById(any())).willThrow(NoSuchElementException.class);

        //when
        ThrowingCallable actual = () -> menuService.hide(뿌링클_세트.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("진열 제외")
    @Test
    void hide() {
        //given
        given(menuRepository.findById(any())).willReturn(Optional.of(맛초킹_세트));

        //when
        Menu menu = menuService.hide(맛초킹_세트.getId());

        //then
        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("모든 메뉴 조회")
    @Test
    void findALl() {
        //given
        given(menuRepository.findAll()).willReturn(Arrays.asList(뿌링클_세트, 맛초킹_세트));

        //when
        List<Menu> actual = menuService.findAll();

        //then
        assertThat(actual).hasSize(2);
    }

}
