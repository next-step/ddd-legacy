package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.MockPurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private final MenuRepository menuRepository = new InMemoryMenuRepository();

    @Spy
    private final PurgomalumClient purgomalumClient = new MockPurgomalumClient();

    @Spy
    private final ProductRepository productRepository = new InMemoryProductRepository();

    @Spy
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    @Spy
    private Menu menuRequest = menuRequest(BigDecimal.valueOf(19000));

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴 생성")
    @Test
    void create() {
        Menu actual = menuRequest;
        Menu expected = menuService.create(actual);

        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(actual.getName()),
                () -> assertThat(expected.getPrice()).isEqualTo(actual.getPrice())
        );
    }

    @DisplayName("메뉴 생성 - null 또는 음수값을 생성될 메뉴의 가격으로 정할 수 없다.")
    @Test
    void createValidationPrice() {
        Menu nullPriceActual = menuRequest;
        when(nullPriceActual.getPrice()).thenReturn(null);
        // null price
        assertThatThrownBy(() -> menuService.create(nullPriceActual))
                .isInstanceOf(IllegalArgumentException.class);

        // negative price
        Menu negativePriceActual = menuRequest;
        when(nullPriceActual.getPrice()).thenReturn(BigDecimal.valueOf(-19000));
        assertThatThrownBy(() -> menuService.create(negativePriceActual))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 - 생성될 메뉴는 이미 존재하는 메뉴 그룹 중 하나에 속해야한다.")
    @Test
    void createValidationMenuGroup() {
        Menu actual = menuRequest;

        when(menuGroupRepository.findById(actual.getMenuGroupId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> menuService.create(actual))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 생성 - 생성될 메뉴에 포함되는 단품의 수량은 1개 이상이어야 한다.")
    @Test
    void createValidationMenuProducts() {
        // null menuProducts
        Menu nullMenuProductsActual = menuRequest;
        when(nullMenuProductsActual.getMenuProducts()).thenReturn(null);
        assertThatThrownBy(() -> menuService.create(nullMenuProductsActual))
                .isInstanceOf(IllegalArgumentException.class);

        // null menuProducts
        Menu emptyMenuProductsActual = menuRequest;
        when(emptyMenuProductsActual.getMenuProducts()).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> menuService.create(nullMenuProductsActual))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 생성 - 생성될 메뉴에 포함되는 단품들은 이미 존재하는 단품들이어야 한다.")
    @Test
    void createValidationProduct() {
        Menu actual = menuRequest;

        when(menuGroupRepository.findById(actual.getMenuGroupId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> menuService.create(actual))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 생성 - 생성될 메뉴의 가격은 생성될 메뉴에 포함되는 단품 가격의 모든 합보다 클 수 없다.")
    @Test
    void createValidationMenuPrice() {
        Menu actual = menuRequest;

        when(actual.getPrice()).thenReturn(BigDecimal.valueOf(99999999));
        assertThatThrownBy(() -> menuService.create(actual))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 - 비속어를 생성될 메뉴의 이름으로 정할 수 없다.")
    @Test
    void createValidationMenuNameProfanity() {

        // contains profanity
        Menu profanityActual = menuRequest;
        when(purgomalumClient.containsProfanity(profanityActual.getName())).thenReturn(true);
        assertThatThrownBy(() -> menuService.create(profanityActual))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 가격 변경")
    @Test
    void changePrice() {
        Menu priceChangeRequest = spy(menuService.create(menuRequest));

        BigDecimal changePrice = BigDecimal.valueOf(10000);
        when(priceChangeRequest.getPrice()).thenReturn(changePrice);

        Menu expected = menuService.changePrice(priceChangeRequest.getId(), priceChangeRequest);
        assertThat(expected.getPrice()).isEqualTo(changePrice);
    }


    @DisplayName("메뉴 가격 변경 - null 으로 가격을 변경할 수 없다.")
    @Test
    void changePriceValidationNull() {
        Menu priceChangeRequest = spy(menuService.create(menuRequest));

        when(priceChangeRequest.getPrice()).thenReturn(null);
        assertThatThrownBy(() -> menuService.changePrice(priceChangeRequest.getId(), priceChangeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격 변경 - 음수값으로 가격을 변경할 수 없다.")
    @Test
    void changePriceValidationNegative() {
        Menu priceChangeRequest = spy(menuService.create(menuRequest));

        BigDecimal changePrice = BigDecimal.valueOf(-10000);
        when(priceChangeRequest.getPrice()).thenReturn(changePrice);
        assertThatThrownBy(() -> menuService.changePrice(priceChangeRequest.getId(), priceChangeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격 변경 - 변경하고자 하는 메뉴는 이미 존재하는 메뉴여야 한다.")
    @Test
    void changePriceValidationNotExists() {
        Menu actual = spy(menuService.create(menuRequest));

        when(menuRepository.findById(actual.getId())).thenThrow(NoSuchElementException.class);
        assertThatThrownBy(() -> menuService.changePrice(actual.getId(), actual))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 가격 변경 - 변경될 메뉴의 가격은 생성될 메뉴에 포함되는 단품 가격의 모든 합보다 클 수 없다.")
    @Test
    void changePriceValidationSum() {


    }

    @DisplayName("메뉴 노출")
    @Test
    void display() {
        Menu actual = menuService.create(menuRequest);
        Menu expected = menuService.display(actual.getId());

        assertThat(expected.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴 노출 - 존재하지 않는 메뉴에 대한 메뉴 노출 처리")
    @Test
    void displayValidation() {
        Menu actual = spy(menuService.create(menuRequest));

        when(menuRepository.findById(actual.getId())).thenThrow(NoSuchElementException.class);
        assertThatThrownBy(() -> menuService.display(actual.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 숨김")
    @Test
    void hide() {
        Menu actual = menuService.create(menuRequest);
        Menu expected = menuService.hide(actual.getId());

        assertThat(expected.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴 숨김 - 존재하지 않는 메뉴에 대한 메뉴 숨김 처리")
    @Test
    void hideValidation() {
        Menu actual = spy(menuService.create(menuRequest));
        when(menuRepository.findById(actual.getId())).thenThrow(NoSuchElementException.class);
        assertThatThrownBy(() -> menuService.hide(actual.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void findAll() {
        menuService.create(menuRequest);
        menuService.create(menuRequest);
        assertThat(menuRepository.findAll().size()).isEqualTo(2);
    }

    private Menu menuRequest(BigDecimal menuPrice) {
        Menu menuRequest = new Menu();
        menuRequest.setName("후라이드+후라이드");
        menuRequest.setPrice(menuPrice);
        menuRequest.setMenuGroupId(saveMenuGroup().getId());
        menuRequest.setDisplayed(true);

        menuRequest.setMenuProducts(menuProducts());
        return menuRequest;
    }

    private MenuGroup saveMenuGroup() {
        return menuGroupRepository.save(new MenuGroup());
    }

    private List<MenuProduct> menuProducts() {
        List<MenuProduct> menuProducts = new ArrayList<>();
        Product product = productRepository.save(new Product("후라이드", BigDecimal.valueOf(10000)));
        menuProducts.add(new MenuProduct(product, 2));
        return menuProducts;
    }

}