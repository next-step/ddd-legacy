package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

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
    private MenuService target;

    private static final UUID MENU_GROUP_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID INVALID_ID = UUID.randomUUID();
    private static final UUID MENU_ID = UUID.randomUUID();

    private MenuGroup buildValidMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);

        return menuGroup;
    }

    private Product buildValidProduct() {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setPrice(BigDecimal.ONE);

        return product;
    }

    private MenuProduct buildValidMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(PRODUCT_ID);
        menuProduct.setProduct(buildValidProduct());
        menuProduct.setQuantity(1L);

        return menuProduct;
    }

    private Menu buildValidMenu() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroupId(MENU_GROUP_ID);
        menu.setMenuProducts(List.of(buildValidMenuProduct()));
        menu.setName("양념치킨");

        return menu;
    }

    @BeforeEach
    void initTest() {
        Mockito.lenient().when(menuRepository.findById(MENU_ID)).thenReturn(Optional.of(buildValidMenu()));
        Mockito.lenient().when(menuGroupRepository.findById(MENU_GROUP_ID)).thenReturn(Optional.of(buildValidMenuGroup()));
        Mockito.lenient().when(menuGroupRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        Mockito.lenient().when(productRepository.findAllByIdIn(List.of(PRODUCT_ID))).thenReturn(List.of(buildValidProduct()));
        Mockito.lenient().when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(buildValidProduct()));
        Mockito.lenient().when(purgomalumClient.containsProfanity("fuck")).thenReturn(true);
        Mockito.lenient().when(purgomalumClient.containsProfanity("asshole")).thenReturn(true);
    }

    @Test
    @DisplayName("정상생성")
    void create() {
        Menu request = buildValidMenu();

        target.create(request);

        Mockito.verify(menuRepository).save(any());
    }

    @Test
    @DisplayName("메뉴는 그룹에 속한다.")
    void noInvalidMenuGroup() {
        Menu request = buildValidMenu();
        request.setMenuGroupId(INVALID_ID);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴는 1개 이상의 상품으로 이루어진다.")
    void noEmptyProduct() {
        Menu request = buildValidMenu();
        request.setMenuProducts(new ArrayList<>());

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격은 음수일 수 없다.")
    void noMinusPriceMenu() {
        Menu request = buildValidMenu();
        request.setPrice(BigDecimal.valueOf(-1L));

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("디피된 메뉴의 가격은 구성 상품의 가격의 총합보다 클 수 없다.")
    void menuPriceNotGreaterThanProductPriceSum() {
        Menu request = buildValidMenu();
        request.setPrice(BigDecimal.TEN);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("메뉴의 이름에 비속어를 쓸 수 없다.")
    @ValueSource(strings = {"fuck", "asshole"})
    void noBadName(String badName) {
        Menu request = buildValidMenu();
        request.setName(badName);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격은 변경 가능하다.")
    void changePrice() {
        Menu request = buildValidMenu();
        request.setPrice(BigDecimal.ZERO);

        Menu result = target.changePrice(MENU_ID, request);

        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @Test
    @DisplayName("디피된 메뉴의 가격은 구성 상품의 가격의 총합보다 클 수 없다.")
    void cannotChangeMenuPriceGreaterThanProductPriceSum() {
        Menu request = buildValidMenu();
        request.setPrice(BigDecimal.TEN);

        assertThatThrownBy(() -> {
            target.changePrice(MENU_ID, request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 디피 여부는 변경 가능하다.")
    void hideDisplay() {
        Menu result = target.hide(MENU_ID);

        assertThat(result.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("디피된 메뉴의 가격은 구성 상품의 가격의 총합보다 클 수 없다.")
    void cannotDisplayMenuPriceGreaterThanProductPriceSum() {
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(1L);
        menuProduct.setProduct(product);

        Menu menu = new Menu();
        menu.setDisplayed(false);
        menu.setPrice(BigDecimal.TEN);
        menu.setMenuProducts(List.of(menuProduct));

        Mockito.when(menuRepository.findById(MENU_ID)).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> {
            target.display(MENU_ID);
        })
                .isInstanceOf(IllegalStateException.class);
    }
}