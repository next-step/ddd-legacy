package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.fixture.KitchenposFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    private Menu menu;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menu = new Menu();
    }

    @DisplayName("메뉴의 가격이 입력되지 않으면 메뉴를 등록할 수 없다.")
    @ParameterizedTest
    @NullSource
    void emptyMenuPrice(BigDecimal price) {
        menu.setPrice(price);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 0 이하면 메뉴는 등록할 수 없다.")
    @Test
    void negativeInteger() {
        menu.setPrice(BigDecimal.valueOf(-1));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴 상품에 속해 있는 상품들의 총 가격의 합보다 클 수 없다.")
    @Test
    void sumPrice() {
        MenuGroup menuGroup = menuGroup();
        Product chicken = chickenProduct();
        Product pasta = pastaProduct();
        Menu menu = menu(menuGroup, chicken, pasta);
        BigDecimal price = chicken.getPrice().add(pasta.getPrice()).add(BigDecimal.valueOf(1000));
        menu.setPrice(price);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(Arrays.asList(chicken, pasta));
        when(productRepository.findById(chicken.getId())).thenReturn(Optional.of(chicken));
        when(productRepository.findById(pasta.getId())).thenReturn(Optional.of(pasta));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름이 입력되지 않으면 메뉴를 등록할 수 없다.")
    @ParameterizedTest
    @NullSource
    void nullName(String name) {
        MenuGroup menuGroup = menuGroup();
        Product chicken = chickenProduct();
        Product pasta = pastaProduct();
        Menu menu = menu(menuGroup, chicken, pasta);
        menu.setName(name);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(Arrays.asList(chicken, pasta));
        when(productRepository.findById(chicken.getId())).thenReturn(Optional.of(chicken));
        when(productRepository.findById(pasta.getId())).thenReturn(Optional.of(pasta));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름에 나쁜말이 포함되어 있으면 메뉴를 등록할 수 없다.")
    @Test
    void badName() {
        MenuGroup menuGroup = menuGroup();
        Product chicken = chickenProduct();
        Product pasta = pastaProduct();
        Menu menu = menu(menuGroup, chicken, pasta);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(Arrays.asList(chicken, pasta));
        when(productRepository.findById(chicken.getId())).thenReturn(Optional.of(chicken));
        when(productRepository.findById(pasta.getId())).thenReturn(Optional.of(pasta));
        when(purgomalumClient.containsProfanity(anyString())).thenReturn(true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품이 없으면 메뉴를 등록할 수 없다.")
    @Test
    void noMenuProduct() {
        MenuGroup menuGroup = menuGroup();
        Product chicken = chickenProduct();
        Product pasta = pastaProduct();
        Menu menu = menu(menuGroup, chicken, pasta);

        menu.setMenuProducts(new ArrayList<>());

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹을 입력해 주지 않으면 메뉴로 등록할 수 없다.")
    @Test
    void noMenuGroup() {
        Product chicken = chickenProduct();
        Product pasta = pastaProduct();
        Menu menu = menu(null, chicken, pasta);

        menu.setMenuProducts(new ArrayList<>());

        when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 상품의 상품 수량은 0 이하 일 수 없다.")
    @Test
    void negativeMenuProductQuantity() {
        MenuGroup menuGroup = menuGroup();
        Product chickenProduct = chickenProduct();
        Product pastaProduct = pastaProduct();
        MenuProduct chickenMenuProduct = menuProduct(chickenProduct, -1);
        MenuProduct pastaMenuProduct = menuProduct(pastaProduct, 1);
        Menu menu = menuWithMenuProduct(menuGroup, chickenMenuProduct, pastaMenuProduct);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(Arrays.asList(chickenProduct, pastaProduct));

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
