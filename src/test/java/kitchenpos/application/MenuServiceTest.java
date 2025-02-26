package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);

    private final MenuService menuService = new MenuService(menuRepository, menuGroupRepository,
        productRepository, purgomalumClient);

    @ParameterizedTest
    @DisplayName("메뉴 등록 시 가격이 없거나 0 보다 작으면 예외를 발생 시킨다.")
    @ValueSource(ints = {-1, -2})
    void create(int price) {
        var request = new Menu();
        request.setPrice(BigDecimal.valueOf(price));

        assertThatThrownBy(
            () -> menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 가격은 0보다 작을 수 없습니다.");
    }

    @Test
    @DisplayName("메뉴 등록 시 요청 상품이 없는 경우 예외를 발생 시킨다.")
    void createWithoutProduct() {
        var request = new Menu();
        request.setMenuGroupId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(10_000));
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));

        assertThatThrownBy(
            () -> menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 구성 상품이 없습니다.");
    }

    @Test
    @DisplayName("메뉴 등록 시 요청 상품 수량이 0보다 작은 경우 예외를 발생 시킨다.")
    void createWithEmptyProduct() {
        var request = new Menu();
        request.setMenuGroupId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(10_000));

        var menuProduct = new MenuProduct();
        menuProduct.setQuantity(-1L);
        request.setMenuProducts(List.of(menuProduct));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(new Product()));

        assertThatThrownBy(
            () -> menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 구성 상품 수량은 0보다 작을 수 없습니다.");
    }

    @Test
    @DisplayName("메뉴 가격이 구성 상품 가격 합 보다 클 경우 예외를 발생 시킨다.")
    void createWithWrongPrice() {
        var menuProduct = new MenuProduct();
        menuProduct.setQuantity(2L);

        var product = new Product();
        product.setPrice(BigDecimal.valueOf(10_000));

        var request = new Menu();
        request.setMenuGroupId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(50_000));
        request.setMenuProducts(List.of(menuProduct));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        assertThatThrownBy(
            () -> menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 가격은 상품의 총 합보다 클 수 없습니다.");
    }

    @Test
    @DisplayName("메뉴 이름에 비속어가 포함되면 예외를 발생 시킨다.")
    void createWithBadName() {
        var menuProduct = new MenuProduct();
        menuProduct.setQuantity(2L);

        var product = new Product();
        product.setPrice(BigDecimal.valueOf(10_000));

        var request = new Menu();
        request.setMenuGroupId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(10_000));
        request.setMenuProducts(List.of(menuProduct));
        request.setName("욕설");

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity("욕설")).thenReturn(Boolean.TRUE);

        assertThatThrownBy(
            () -> menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("비속어는 사용할 수 없습니다.");
    }


    @Test
    @DisplayName("가격 수정 시 0보다 작은 값을 입력하면 예외를 발생 시킨다.")
    void changePrice() {
        var request = new Menu();
        request.setPrice(BigDecimal.valueOf(-1));

        assertThatThrownBy(
            () -> menuService.changePrice(UUID.randomUUID(), request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 가격은 0보다 작을 수 없습니다.");
    }

    @Test
    @DisplayName("가격 수정 시 메뉴 가격이 구성 상품의 총합보다 클 경우 예외를 발생 시킨다.")
    void changePriceWithGreaterThanProductPrice() {
        var request = new Menu();
        request.setPrice(BigDecimal.valueOf(50_000));

        var menu = getMenu();
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatThrownBy(
            () -> menuService.changePrice(UUID.randomUUID(), request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 가격은 상품의 총 합보다 클 수 없습니다.");
    }

    @NotNull
    private static Menu getMenu() {
        var product = new Product();
        product.setPrice(BigDecimal.valueOf(10_000));

        var menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);

        var menu = new Menu();
        menu.setMenuProducts(List.of(menuProduct));
        return menu;
    }

    @Test
    @DisplayName("메뉴 표시하기 시 메뉴 가격이 구성 상품의 총합보다 클 경우 예외를 발생 시킨다.")
    void displayWithBadPrice() {
        var menu = getMenu();
        menu.setPrice(BigDecimal.valueOf(50_000));

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatThrownBy(
            () -> menuService.display(UUID.randomUUID())
        ).isInstanceOf(IllegalStateException.class)
            .hasMessage("메뉴 가격은 상품의 총 합보다 클 수 없습니다.");
    }

    @Test
    @DisplayName("메뉴 표시하기")
    void display() {
        var menu = getMenu();
        menu.setPrice(BigDecimal.valueOf(10_000));

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        var response = menuService.display(UUID.randomUUID());
        assertThat(response.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴 숨기기")
    void hide() {
        var menu = getMenu();
        menu.setPrice(BigDecimal.valueOf(10_000));

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        var response = menuService.hide(UUID.randomUUID());
        assertThat(response.isDisplayed()).isFalse();
    }
}