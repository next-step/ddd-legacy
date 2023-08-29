package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
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

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
import static kitchenpos.fixture.ProductFixtures.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;
    private Product product;
    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        product = createProduct("상품1", new BigDecimal("10000"));
        menuGroup = createMenuGroup("메뉴그룹1");
    }

    @Test
    void 메뉴는_등록할_수_있다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴1", new BigDecimal("2000"), menuGroup, false, List.of(menuProduct));

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any()))
                .willReturn(List.of(product));
        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any()))
                .willReturn(false);
        given(menuRepository.save(any()))
                .willReturn(menu);

        //when
        Menu result = menuService.create(menu);

        //then
        assertThat(result.getName()).isEqualTo(menu.getName());
        assertThat(result.getPrice()).isEqualTo(menu.getPrice());
    }

    @Test
    void 메뉴의_가격은_비어있을_수_없다_0원_이상이어야_한다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴", null, menuGroup, false, List.of(menuProduct));

        //when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴의_가격은_0원_이상이어야_한다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴", new BigDecimal("-1"), menuGroup, false, List.of(menuProduct));

        //when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴그룹_값을_필수이다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴", new BigDecimal("1000"), menuGroup, false, List.of(menuProduct));

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 메뉴상품_목록은_필수로_있어야_한다(List<MenuProduct> menuProducts) {
        //given
        Menu menu = createMenu("메뉴", new BigDecimal("1000"), menuGroup, false, menuProducts);

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(menuGroup));

        //when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_가격은_메뉴_상품들의_총합보다_작아야_한다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴", new BigDecimal("20000"), menuGroup, false, List.of(menuProduct));

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any()))
                .willReturn(List.of(product));
        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));

        //when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴명에_욕설을_포함해서는_안된다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴", new BigDecimal("5000"), menuGroup, false, List.of(menuProduct));

        given(menuGroupRepository.findById(any()))
                .willReturn(Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any()))
                .willReturn(List.of(product));
        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));
        given(purgomalumClient.containsProfanity(any()))
                .willReturn(true);

        //when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_가격은_변경할_수_있다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu request = createMenu("메뉴1", new BigDecimal("2000"), menuGroup, false, List.of(menuProduct));
        Menu menu = createMenu("메뉴1", new BigDecimal("1000"), menuGroup, false, List.of(menuProduct));

        given(menuRepository.findById(any()))
                .willReturn(Optional.of(menu));

        //when
        Menu result = menuService.changePrice(menu.getId(), request);

        //when
        assertThat(result.getPrice()).isEqualTo(menu.getPrice());
    }

    @Test
    void 메뉴_수정요청에서_가격이_비어있다면_수정할_수_없다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴1", null, menuGroup, false, List.of(menuProduct));

        //when, then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_수정요청에서_가격이_0원미만이면_수정할_수_없다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴1", new BigDecimal("-1"), menuGroup, false, List.of(menuProduct));

        //when, then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 수정_후_메뉴의_가격이_메뉴에_속한_상품의_총_가격보다_클_수_없다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴1", new BigDecimal("20000"), menuGroup, false, List.of(menuProduct));

        given(menuRepository.findById(any()))
                .willReturn(Optional.of(menu));

        //when, then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴를_노출할_수_있다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴1", new BigDecimal("2000"), menuGroup, false, List.of(menuProduct));

        given(menuRepository.findById(any()))
                .willReturn(Optional.of(menu));

        //when
        Menu result = menuService.display(menu.getId());

        //then
        assertThat(result.isDisplayed()).isEqualTo(true);
    }

    @Test
    void 메뉴를_노출시에_메뉴의_가격이_상품들의_총합보다_클_수_없다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴1", new BigDecimal("20000"), menuGroup, false, List.of(menuProduct));

        given(menuRepository.findById(any()))
                .willReturn(Optional.of(menu));

        //when, then
        assertThatThrownBy(() -> menuService.display(menu.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 메뉴를_숨길_수_있다() {
        //given
        MenuProduct menuProduct = createMenuProduct(product, 1);
        Menu menu = createMenu("메뉴1", new BigDecimal("2000"), menuGroup, true, List.of(menuProduct));

        given(menuRepository.findById(any()))
                .willReturn(Optional.of(menu));

        //when
        Menu result = menuService.hide(menu.getId());

        //then
        assertThat(result.isDisplayed()).isEqualTo(false);
    }

    @Test
    void 모든_메뉴를_조회할_수_있다() {
        //given
        MenuProduct menuProduct1 = createMenuProduct(product, 1);
        Menu menu1 = createMenu("메뉴1", new BigDecimal("2000"), menuGroup, true, List.of(menuProduct1));
        MenuProduct menuProduct2 = createMenuProduct(product, 1);
        Menu menu2 = createMenu("메뉴1", new BigDecimal("2000"), menuGroup, true, List.of(menuProduct1));

        List<Menu> menus = List.of(menu1, menu2);

        given(menuRepository.findAll())
                .willReturn(menus);

        //when
        List<Menu> result = menuService.findAll();

        //then
        assertThat(result.size()).isEqualTo(menus.size());
    }
}