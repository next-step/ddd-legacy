package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.dummy.DummyMenu;
import kitchenpos.dummy.DummyMenuGroup;
import kitchenpos.dummy.DummyMenuProduct;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

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
    private MenuService menuService;

    private MenuGroup menuGroup;
    private List<MenuProduct> menuProducts;

    @BeforeEach
    void setUp() {
        menuGroup = DummyMenuGroup.defaultMenuGroup();
        menuProducts = List.of(DummyMenuProduct.defaultMenuProduct());

    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    void create() {
        // given
        Menu menu = DummyMenu.createMenu(menuGroup, menuProducts);

        setMockDefault();

        when(menuRepository.save(any())).thenReturn(menu);

        // when
        Menu created = menuService.create(menu);

        // then
        assertAll(
                () -> assertThat(created.getId()).isNotNull(),
                () -> assertThat(created.getName()).isEqualTo(menu.getName()),
                () -> assertThat(created.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(created.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(created.isDisplayed()).isEqualTo(menu.isDisplayed()),
                () -> assertThat(created.getMenuProducts()).isEqualTo(menu.getMenuProducts())
        );
    }

    @DisplayName("메뉴 가격을 변경한다.")
    @Test
    void changePrice_oer_price() {
        // given
        Menu menu = DummyMenu.createMenu(menuGroup, menuProducts);
        menu.setPrice(new BigDecimal(20000));

        setMockDefault();
        when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

        Menu changePrice = menuService.changePrice(menu.getId(), menu);

        // then
        assertAll(
                () -> assertThat(changePrice.getId()).isNotNull(),
                () -> assertThat(changePrice.getName()).isEqualTo(menu.getName()),
                () -> assertThat(changePrice.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(changePrice.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(changePrice.isDisplayed()).isEqualTo(menu.isDisplayed()),
                () -> assertThat(changePrice.getMenuProducts()).isEqualTo(menu.getMenuProducts())
        );
    }

    @DisplayName("메뉴 가격을 작은 값으로 변경한다.")
    @Test
    void changePrice_under_price() {
        // given
        Menu menu = DummyMenu.createMenu(menuGroup, menuProducts);
        menu.setPrice(new BigDecimal(2000));

        when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

        Menu changePrice = menuService.changePrice(menu.getId(), menu);

        // then
        assertAll(
                () -> assertThat(changePrice.getId()).isNotNull(),
                () -> assertThat(changePrice.getName()).isEqualTo(menu.getName()),
                () -> assertThat(changePrice.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(changePrice.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(changePrice.isDisplayed()).isEqualTo(menu.isDisplayed()),
                () -> assertThat(changePrice.getMenuProducts()).isEqualTo(menu.getMenuProducts())
        );
    }

    @DisplayName("메뉴를 노출한다.")
    @Test
    void display() {
        // given
        Menu menu = DummyMenu.createMenu(menuGroup, menuProducts);

        when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

        // when
        Menu display = menuService.display(menu.getId());

        // then
        assertAll(
                () -> assertThat(display.getId()).isNotNull(),
                () -> assertThat(display.getName()).isEqualTo(menu.getName()),
                () -> assertThat(display.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(display.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(display.isDisplayed()).isEqualTo(true),
                () -> assertThat(display.getMenuProducts()).isEqualTo(menu.getMenuProducts())
        );
    }

    @DisplayName("메뉴를 숨긴다.")
    @Test
    void hide() {
        // given
        Menu menu = DummyMenu.createMenu(menuGroup, menuProducts);

        when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

        // when
        Menu hide = menuService.hide(menu.getId());

        // then
        assertAll(
                () -> assertThat(hide.getId()).isNotNull(),
                () -> assertThat(hide.getName()).isEqualTo(menu.getName()),
                () -> assertThat(hide.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(hide.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(hide.isDisplayed()).isEqualTo(false),
                () -> assertThat(hide.getMenuProducts()).isEqualTo(menu.getMenuProducts())
        );
    }

    @DisplayName("전체 메뉴를 조회한다.")
    @Test
    void findAll() {
        // given
        Menu menu = DummyMenu.createMenu(menuGroup, menuProducts);

        when(menuRepository.findAll()).thenReturn(List.of(menu));

        // when
        List<Menu> all = menuService.findAll();

        // then
        assertAll(
                () -> assertThat(all).isNotNull(),
                () -> assertThat(all.size()).isSameAs(1)
        );

    }

    private void setMockDefault() {

        when(menuGroupRepository.findById(menuGroup.getId())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(DummyMenuProduct.defaultMenuProduct().getProduct()));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(productRepository.findById(any())).thenReturn(Optional.of(DummyMenuProduct.defaultMenuProduct().getProduct()));

    }

}