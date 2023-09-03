package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private MenuService sut;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        sut = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴를 신규 등록한다")
    @Test
    void testCreate() {
        // given
        Menu request = MenuFixture.create();

        given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(MenuGroupFixture.create()));
        given(productRepository.findAllByIdIn(any(List.class))).willReturn(List.of(ProductFixture.create()));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(ProductFixture.create()));
        given(menuRepository.save(any(Menu.class))).willReturn(request);

        // when
        Menu actual = sut.create(request);

        // then
        assertThat(actual).isEqualTo(request);
    }

    @DisplayName("메뉴는 0원 미만의 가격으로 등록할 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {-1_000, -500})
    void testCreateWhenPriceIsNegative(int price) {
        // given
        Menu request = MenuFixture.create(price);

        // when // then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 포함된 메뉴 상품이 1개 미만이면 등록할 수 없다")
    @Test
    void testCreateWhenMenuProductsIsEmpty() {
        // given
        Menu request = MenuFixture.create(Collections.emptyList());
        given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(MenuGroupFixture.create()));

        // when // then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 포함된 메뉴 상품들의 가격 합이 메뉴 가격보다 작으면 등록할 수 없다")
    @Test
    void testCreateWhenMenuProductsTotalPriceIsLessThanMenuPrice() {
        // given
        Product product = ProductFixture.create(10_000);
        MenuProduct menuProduct = MenuProductFixture.create(product, 2);
        Menu request = MenuFixture.create(22_000, List.of(menuProduct));

        given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(MenuGroupFixture.create()));
        given(productRepository.findAllByIdIn(any(List.class))).willReturn(List.of(product));
        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));

        // when // then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 수정한다.")
    @Test
    void testChangePrice() {
        // given
        Menu menu = MenuFixture.create();
        int expectedPrice = 5_000;

        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        // when
        Menu actual = sut.changePrice(menu.getId(), MenuFixture.create(expectedPrice));

        // then
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(expectedPrice));
    }

    @DisplayName("존재하지 않는 메뉴 id로는 메뉴 가격을 수정할 수 없다")
    @Test
    void testChangePriceWhenNotExistMenuId() {
        // given
        Menu menu = MenuFixture.create();
        int expectedPrice = 5_000;

        given(menuRepository.findById(menu.getId())).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> sut.changePrice(menu.getId(), MenuFixture.create(expectedPrice)))
            .isExactlyInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("0 미만의 가격으로는 메뉴 가격을 수정할 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {-10_000, -5_000})
    void testChangePriceWhenNegativePrice(int price) {
        // given
        Menu menu = MenuFixture.create();

        // when // then
        assertThatThrownBy(() -> sut.changePrice(menu.getId(), MenuFixture.create(price)))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("특정 메뉴를 노출 처리한다")
    @Test
    void testDisplay() {
        // given
        Menu menu = MenuFixture.create();

        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        // when
        Menu actual = sut.display(menu.getId());

        // then
        assertThat(actual.isDisplayed()).isTrue();
    }

    @DisplayName("특정 메뉴를 숨김 처리한다")
    @Test
    void testHide() {
        // given
        Menu menu = MenuFixture.create();

        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        // when
        Menu actual = sut.hide(menu.getId());

        // then
        assertThat(actual.isDisplayed()).isFalse();
    }

    @DisplayName("모든 메뉴를 조회한다")
    @Test
    void testFindAll() {
        // given
        List<Menu> expected = List.of(MenuFixture.create("testMenu1"), MenuFixture.create("testMenu2"));

        given(menuRepository.findAll()).willReturn(expected);

        // when
        List<Menu> actual = sut.findAll();

        // then
        assertThat(actual.size()).isEqualTo(expected.size());
        assertThat(actual.get(0).getId()).isEqualTo(expected.get(0).getId());
        assertThat(actual.get(1).getId()).isEqualTo(expected.get(1).getId());
    }
}
