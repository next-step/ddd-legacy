package kitchenpos.menu.service;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.menu.fixture.MenuFixture;
import kitchenpos.menu.fixture.MenuProductFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DisplayName("메뉴 서비스 테스트")
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

    private MenuFixture menuFixture;

    @BeforeEach
    void setUp() {
        menuFixture = new MenuFixture();
    }

    @Test
    @DisplayName("새로운 메뉴를 추가할 수 있다.")
    void create() {
        Menu 메뉴 = menuFixture.메뉴_A_가격_10000;

        mockingMenuGroupRepositoryForCreate(메뉴);
        mockingProductRepositoryForCreate(메뉴);
        mockingPurgomalumClientForCreate(false);
        mockingMenuRepositoryForCreate();
        Menu result = menuService.create(메뉴);

        Assertions.assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("메뉴는 반드시 메뉴 그룹에 포함 되어야 한다.")
    void create_exception_menuGroup() {
        Menu 메뉴_그룹_없는_메뉴 = menuFixture.메뉴_그룹_없는_메뉴;

        Assertions.assertThatThrownBy(
                () -> menuService.create(메뉴_그룹_없는_메뉴)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴는 반드시 가격 정보를 가지고 있어야 한다.")
    void create_exception_price_null() {
        Menu 가격_없는_메뉴 = menuFixture.가격_없는_메뉴;

        Assertions.assertThatThrownBy(
                () -> menuService.create(가격_없는_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴는 반드시 상품 정보를 가지고 있어야 한다.")
    void create_exception_menuProduct_null() {
        Menu 상품_없는_메뉴 = menuFixture.상품_없는_메뉴;

        mockingMenuGroupRepositoryForCreate(상품_없는_메뉴);

        Assertions.assertThatThrownBy(
                () -> menuService.create(상품_없는_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴는 반드시 이름 정보를 가지고 있어야 한다.")
    void create_exception_name_null() {
        Menu 이름_없는_메뉴 = menuFixture.이름_없는_메뉴;

        mockingMenuGroupRepositoryForCreate(이름_없는_메뉴);
        mockingProductRepositoryForCreate(이름_없는_메뉴);

        Assertions.assertThatThrownBy(
                () -> menuService.create(이름_없는_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 추가 시 메뉴의 이름이 부적절한지 검사한다.")
    void create_exception_containsProfanity() {
        Menu 부적절한_이름_메뉴 = menuFixture.부적절한_이름_메뉴;

        mockingMenuGroupRepositoryForCreate(부적절한_이름_메뉴);
        mockingProductRepositoryForCreate(부적절한_이름_메뉴);
        mockingPurgomalumClientForCreate(true);

        Assertions.assertThatThrownBy(
                () -> menuService.create(부적절한_이름_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 상품 수량은 기존 상품의 수량과 같아야 한다.")
    void create_exception_same_quantity() {
        Menu 메뉴 = menuFixture.메뉴_A_가격_10000;
        List<MenuProduct> newMenuProduct = new ArrayList<>(메뉴.getMenuProducts());
        newMenuProduct.add(new MenuProductFixture().메뉴_상품);

        mockingMenuGroupRepositoryForCreate(메뉴);
        메뉴.setMenuProducts(newMenuProduct);

        Assertions.assertThatThrownBy(
                () -> menuService.create(메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격은 상품 포함된 상품의 총 가격 보다 클 수 없다.")
    void create_exception_price_difference() {
        Menu 상품_가격보다_큰_메뉴 = menuFixture.메뉴_가격_11000_상품_가격_10000;

        mockingMenuGroupRepositoryForCreate(상품_가격보다_큰_메뉴);
        mockingProductRepositoryForCreate(상품_가격보다_큰_메뉴);

        Assertions.assertThatThrownBy(
                () -> menuService.create(상품_가격보다_큰_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private void mockingMenuGroupRepositoryForCreate(Menu menu) {
        Mockito.when(menuGroupRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(MenuFixture.extractMenuGroupFrom(menu)));
    }

    private void mockingProductRepositoryForCreate(Menu menu) {
        Mockito.when(productRepository.findAllByIdIn(Mockito.any()))
                .thenReturn(MenuFixture.extractProductsFrom(menu));
        Mockito.when(productRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(MenuFixture.extractProductFrom(menu)));
    }

    private void mockingPurgomalumClientForCreate(boolean result) {
        Mockito.when(purgomalumClient.containsProfanity(Mockito.any()))
                .thenReturn(result);
    }

    private void mockingMenuRepositoryForCreate() {
        Mockito.when(menuRepository.save(Mockito.any()))
                .then(AdditionalAnswers.returnsFirstArg());
    }

    @Test
    @DisplayName("메뉴의 가격을 변경할 수 있다.")
    void changePrice() {
        Menu 메뉴_A = menuFixture.메뉴_A_가격_10000;
        Menu 메뉴_B = menuFixture.메뉴_B_가격_5000;

        mockingMenuRepositoryFindBy(메뉴_A);
        menuService.changePrice(메뉴_A.getId(), 메뉴_B);

        Assertions.assertThat(메뉴_A.getPrice()).isEqualTo(메뉴_B.getPrice());
    }

    @Test
    @DisplayName("메뉴의 가격은 해당 상품 총 가격 보다 클 수 없다.")
    void changePrice_exception_price() {
        Menu 메뉴_B = menuFixture.메뉴_B_가격_5000;
        Menu 메뉴_A = menuFixture.메뉴_A_가격_10000;

        mockingMenuRepositoryFindBy(메뉴_B);

        Assertions.assertThatThrownBy(
                () -> menuService.changePrice(메뉴_B.getId(), 메뉴_A)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 노출할 수 있다.")
    void display() {
        Menu 메뉴_C = menuFixture.메뉴_C_가격_100000;

        mockingMenuRepositoryFindBy(메뉴_C);
        menuService.display(메뉴_C.getId());

        Assertions.assertThat(메뉴_C.isDisplayed()).isEqualTo(true);
    }

    @Test
    @DisplayName("메뉴를 숨길 수 있다.")
    void hide() {
        Menu 메뉴_A = menuFixture.메뉴_A_가격_10000;

        mockingMenuRepositoryFindBy(메뉴_A);
        menuService.hide(메뉴_A.getId());

        Assertions.assertThat(메뉴_A.isDisplayed()).isEqualTo(false);
    }

    private void mockingMenuRepositoryFindBy(Menu menu) {
        Mockito.when(menuRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(menu));
    }
}
