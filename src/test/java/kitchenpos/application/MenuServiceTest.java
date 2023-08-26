package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.ProductFixtures.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public
class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService sut;

    private final static UUID uuid = UUID.randomUUID();

    private Product product;
    private MenuProduct menuProduct;

    @BeforeEach
    void setUp() {
        product = createProduct();
        menuProduct = createMenuProduct();
    }

    @DisplayName("메뉴의 가격이 null이면 메뉴를 생성할 수 없다")
    @Test
    void notCreateMenuWithoutPrice() {
        // given
        Menu request = createMenu(null, "메뉴", List.of(menuProduct));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 0미만이면 메뉴를 생성할 수 없다")
    @Test
    void notCreateMenuWithPriceLessThanZero() {
        // given
        Menu request = createMenu(new BigDecimal("-1"), "메뉴", List.of(menuProduct));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴가 메뉴 그룹에 속해 있지 않으면 메뉴를 생성할 수 없다")
    @Test
    void notCreateMenuWithoutMenuGroup() {
        // given
        Menu request = createMenu();

        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(NoSuchElementException.class);
    }

    @ParameterizedTest(name = "메뉴는 최소한 1개 이상의 음식으로 이루어져야 메뉴를 생성할 수 있다: menuProducts = {0}")
    @NullAndEmptySource
    void notCreateMenuWithZeroOrFewerProduct(List<MenuProduct> menuProducts) {
        // given
        Menu request = createMenu(new BigDecimal("1000"), "메뉴", menuProducts);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 최소한 1개 이상의 음식으로 이루어져야 메뉴를 생성할 수 있다")
    @Test
    void notCreateMenuWithZeroOrFewerQuantityOfMenu() {
        // given
        MenuProduct menuProduct = createMenuProduct(product, -1L);
        Menu request = createMenu(new BigDecimal("1000"), "메뉴", List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 음식 가격의 합보다 커진다면 메뉴를 생성할 수 없다")
    @Test
    void notCreateMenuWithPriceGreaterThanSumOfProducts() {
        // given
        Menu request = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름이 null이면 메뉴를 생성할 수 없다")
    @Test
    void notCreateMenuWithoutName() {
        // given
        Menu request = createMenu(new BigDecimal("1000"), null, List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("음식의 이름에 비속어가 포함되어 있으면 메뉴를 생성할 수 없다")
    @Test
    void notCreateMenuWithNameContainingProfanity() {
        // given
        Menu request = createMenu();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 생성할 수 있다")
    @Test
    void create() {
        // given
        Menu request = createMenu();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        given(menuRepository.save(any())).willReturn(new Menu());

        // when
        Menu result = sut.create(request);

        // then
        assertThat(result).isExactlyInstanceOf(Menu.class);
    }

    @DisplayName("메뉴의 가격이 null이면 메뉴의 가격을 수정할 수 없다")
    @Test
    void notChangePriceWithoutPrice() {
        // given
        Menu request = createMenu(null, "메뉴", List.of(menuProduct));

        // when & then
        assertThatThrownBy(() -> sut.changePrice(uuid, request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 0미만이면 메뉴의 가격을 수정할 수 없다")
    @Test
    void notChangePriceWithPriceLessThenZero() {
        // given
        Menu request = createMenu(new BigDecimal("-1"), "메뉴", List.of(menuProduct));

        // when & then
        assertThatThrownBy(() -> sut.changePrice(uuid, request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 음식 가격의 합보다 커진다면 메뉴의 가격을 수정할 수 없다")
    @Test
    void notChangePriceIfPriceOfMenuIsGreaterThanSumOfProducts() {
        // given
        Menu request = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct));

        given(menuRepository.findById(any())).willReturn(Optional.of(request));

        // when & then
        assertThatThrownBy(() -> sut.changePrice(uuid, request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 수정할 수 있다")
    @Test
    void changePrice() {
        // given
        Menu request = createMenu();

        given(menuRepository.findById(any())).willReturn(Optional.of(request));

        // when
        Menu result = sut.changePrice(uuid, request);

        // then
        assertThat(result).isExactlyInstanceOf(Menu.class);
    }

    @DisplayName("메뉴의 가격이 음식 가격의 합보다 커진다면 메뉴를 화면에 표시할 수 없다")
    @Test
    void notDisplayMenuIfPriceOfMenuIsGreaterthanSumOfProducts() {
        // given
        Menu menu = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct));

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.display(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("메뉴를 화면에 표시할 수 있다")
    @Test
    void displayMenu() {
        // given
        Menu menu = createMenu();

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        sut.display(uuid);

        // then
        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 화면에서 숨길 수 있다")
    @Test
    void hideMenu() {
        // given
        Menu menu = createMenu();

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        sut.hide(uuid);

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }
}
