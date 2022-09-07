package kitchenpos.menu;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.product.ProductFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kitchenpos.menu.MenuFixture.changeMenuRequest;
import static kitchenpos.menu.MenuFixture.extractProducts;
import static kitchenpos.menugroup.MenuGroupFixture.menuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    private MenuGroup 추천메뉴;
    private Product 양념치킨;
    private Product 간장치킨;

    @BeforeEach
    void setUp() {
        추천메뉴 = menuGroup("추천메뉴");
        양념치킨 = ProductFixture.product("양념치킨", 20000);
        간장치킨 = ProductFixture.product("간장치킨", 22000);
    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void create() {
        Menu menu = MenuFixture.positiveCountMenu("추천메뉴", 20000, 양념치킨, 간장치킨);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(추천메뉴));
        when(productRepository.findAllByIdIn(any())).thenReturn(extractProducts(menu));
        when(productRepository.findById(양념치킨.getId())).thenReturn(Optional.of(양념치킨));
        when(productRepository.findById(간장치킨.getId())).thenReturn(Optional.of(간장치킨));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(menuRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Menu createdMenu = menuService.create(menu);

        assertAll(
                () -> assertThat(menu.getName()).isEqualTo(createdMenu.getName()),
                () -> assertThat(menu.getPrice()).isEqualTo(createdMenu.getPrice()),
                () -> assertThat(menu.isDisplayed()).isEqualTo(createdMenu.isDisplayed())
        );
    }

    @DisplayName("메뉴 상품이 없으면 메뉴를 생성할 수 없다.")
    @Test
    void createWithNullMenuProduct() {
        Menu menu = MenuFixture.positiveCountMenu("추천메뉴", 20000);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(추천메뉴));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품의 개수가 음수인 메뉴를 생성할 수 없다.")
    @Test
    void createWithNegativeQuantity() {
        Menu menu = MenuFixture.negativeCountMenu("추천메뉴", 20000, 양념치킨, 간장치킨);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(추천메뉴));
        when(productRepository.findAllByIdIn(any())).thenReturn(extractProducts(menu));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 메뉴 상품의 가격(메뉴 상품의 개수 * 상품의 가격)의 합보다 크면 안된다.")
    @Test
    void createWithHigherPriceThanMenuProduct() {
        Menu menu = MenuFixture.positiveCountMenu("추천메뉴", 43000, 양념치킨, 간장치킨);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(추천메뉴));
        when(productRepository.findAllByIdIn(any())).thenReturn(extractProducts(menu));
        when(productRepository.findById(양념치킨.getId())).thenReturn(Optional.of(양념치킨));
        when(productRepository.findById(간장치킨.getId())).thenReturn(Optional.of(간장치킨));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이름이 없는 메뉴는 생성할 수 없다.")
    @Test
    void createWithNullName() {
        Menu menu = MenuFixture.positiveCountMenu(null, 43000, 양념치킨, 간장치킨);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(추천메뉴));
        when(productRepository.findAllByIdIn(any())).thenReturn(extractProducts(menu));
        when(productRepository.findById(양념치킨.getId())).thenReturn(Optional.of(양념치킨));
        when(productRepository.findById(간장치킨.getId())).thenReturn(Optional.of(간장치킨));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 변경한다.")
    @Test
    void change() {
        Menu menu = MenuFixture.positiveCountMenu("추천메뉴", 30000, 양념치킨, 간장치킨);
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        Menu changePrice = menuService.changePrice(menu.getId(), changeMenuRequest(20000));

        assertThat(changePrice.getPrice()).isEqualTo(BigDecimal.valueOf(20000));
    }

    @DisplayName("메뉴의 가격을 0원 미만으로 변경할 수 없다.")
    @Test
    void changeWithNegative() {
        Menu 메뉴 = MenuFixture.positiveCountMenu("추천메뉴", 30000, 양념치킨, 간장치킨);

        assertThatThrownBy(() -> menuService.changePrice(메뉴.getId(), changeMenuRequest(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 포함된 각 메뉴 상품의 가격보다 크도록 변경할 수 없다.")
    @Test
    void changeWithHigherPriceThanMenuProduct() {
        Menu 메뉴 = MenuFixture.positiveCountMenu("추천메뉴", 30000, 양념치킨, 간장치킨);
        when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴));

        assertThatThrownBy(() -> menuService.changePrice(메뉴.getId(), changeMenuRequest(20001)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 전시한다.")
    @Test
    void display() {
        Menu 메뉴 = MenuFixture.hideMenu("추천메뉴", 20000, 양념치킨, 간장치킨);
        when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴));

        Menu display = menuService.display(메뉴.getId());

        assertThat(display.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 숨긴다.")
    @Test
    void hide() {
        Menu 메뉴 = MenuFixture.positiveCountMenu("추천메뉴", 20000, 양념치킨, 간장치킨);
        when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴));

        Menu display = menuService.hide(메뉴.getId());

        assertThat(display.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴 목록을 조회한다.")
    @Test
    void findAll() {
        Menu 추천메뉴 = MenuFixture.positiveCountMenu("추천메뉴", 20000, 양념치킨, 간장치킨);
        Menu 계절메뉴 = MenuFixture.positiveCountMenu("계절메뉴", 19000, 양념치킨, 간장치킨);
        Menu 이달의메뉴 = MenuFixture.positiveCountMenu("이달의메뉴", 18000, 양념치킨, 간장치킨);
        when(menuRepository.findAll()).thenReturn(List.of(추천메뉴, 계절메뉴, 이달의메뉴));

        List<String> 메뉴목록 = menuService.findAll().stream()
                .map(Menu::getName)
                .collect(Collectors.toList());

        assertThat(메뉴목록).containsExactly("추천메뉴", "계절메뉴", "이달의메뉴");
    }
}
