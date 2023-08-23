package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
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

import static java.util.Collections.emptyList;
import static kitchenpos.application.ProductServiceTest.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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
        product = createProduct("햄버거", new BigDecimal("1000"));
        menuProduct = createMenuProduct(product, 1L);
    }

    @Test
    void 메뉴의_가격이_null이면_메뉴를_생성할_수_없다() {
        // given
        Menu request = createMenu(null, "메뉴", uuid, List.of(menuProduct));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴의_가격이_0미만이면_메뉴를_생성할_수_없다() {
        // given
        Menu request = createMenu(new BigDecimal("-1"), "메뉴", uuid, List.of(menuProduct));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴가_메뉴_그룹에_속해_있지_않으면_메뉴를_생성할_수_없다() {
        // given
        Menu request = createMenu(new BigDecimal("1000"), "메뉴", uuid, List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(NoSuchElementException.class);
    }

    @ParameterizedTest(name = "메뉴는_최소한_1개_이상의_음식으로_이루어져야_메뉴를_생성할_수_있다: menuProducts = {0}")
    @NullAndEmptySource
    void 메뉴는_최소한_1개_이상의_음식으로_이루어져야_메뉴를_생성할_수_있다(List<MenuProduct> menuProducts) {
        // given
        Menu request = createMenu(new BigDecimal("1000"), "메뉴", uuid, menuProducts);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴에_포함된_음식_중_하나라도_이미_생성된_상태가_아니라면_메뉴를_생성할_수_없다() {
        // given
        Menu request = createMenu(new BigDecimal("1000"), "메뉴", uuid, List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(emptyList());

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void 메뉴에_들어가는_음식의_양이_0미만이면_메뉴를_생성할_수_없다() {
        // given
        MenuProduct menuProduct = createMenuProduct(product, -1L);
        Menu request = createMenu(new BigDecimal("1000"), "메뉴", uuid, List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴의_가격이_음식_가격의_합보다_커진다면_메뉴를_생성할_수_없다() {
        // given
        Menu request = createMenu(new BigDecimal("2000"), "메뉴", uuid, List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴의_이름이_null이면_메뉴를_생성할_수_없다() {
        // given
        Menu request = createMenu(new BigDecimal("1000"), null, uuid, List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 음식의_이름에_비속어가_포함되어_있으면_메뉴를_생성할_수_없다() {
        // given
        Menu request = createMenu(new BigDecimal("1000"), "메뉴", uuid, List.of(menuProduct));

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(new MenuGroup()));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_생성할_수_있다() {
        // given
        Menu request = createMenu(new BigDecimal("1000"), "메뉴", uuid, List.of(menuProduct));

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

    @Test
    void 메뉴의_가격이_null이면_메뉴의_가격을_수정할_수_없다() {
        // given
        Menu request = createMenu(null, "메뉴", uuid, List.of(menuProduct));

        // when & then
        assertThatThrownBy(() -> sut.changePrice(uuid, request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴의_가격이_0미만이면_메뉴의_가격을_수정할_수_없다() {
        // given
        Menu request = createMenu(new BigDecimal("-1"), "메뉴", uuid, List.of(menuProduct));

        // when & then
        assertThatThrownBy(() -> sut.changePrice(uuid, request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴의_가격이_음식_가격의_합보다_커진다면_메뉴의_가격을_수정할_수_없다() {
        // given
        Menu request = createMenu(new BigDecimal("2000"), "메뉴", uuid, List.of(menuProduct));

        given(menuRepository.findById(any())).willReturn(Optional.of(request));

        // when & then
        assertThatThrownBy(() -> sut.changePrice(uuid, request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴의_가격을_수정할_수_있다() {
        // given
        Menu request = createMenu(new BigDecimal("1000"), "메뉴", uuid, List.of(menuProduct));

        given(menuRepository.findById(any())).willReturn(Optional.of(request));

        // when
        Menu result = sut.changePrice(uuid, request);

        // then
        assertThat(result).isExactlyInstanceOf(Menu.class);
    }

    @Test
    void 메뉴의_가격이_음식_가격의_합보다_커진다면_메뉴를_화면에_표시할_수_없다() {
        // given
        Menu menu = createMenu(new BigDecimal("2000"), "메뉴", uuid, List.of(menuProduct));

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.display(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 메뉴를_화면에_표시할_수_있다() {
        // given
        Menu menu = createMenu(new BigDecimal("1000"), "메뉴", uuid, List.of(menuProduct));

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        sut.display(uuid);

        // then
        assertThat(menu.isDisplayed()).isTrue();
    }

    @Test
    void 메뉴를_화면에서_숨길_수_있다() {
        // given
        Menu menu = createMenu(new BigDecimal("1000"), "메뉴", uuid, List.of(menuProduct));

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        sut.hide(uuid);

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }

    public static Menu createMenu(BigDecimal price, String name, UUID menuGroupId, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setName(name);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menu;
    }

    public static MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
