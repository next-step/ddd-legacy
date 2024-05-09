package kitchenpos.menu.service;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.menu.MenuTestHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    @DisplayName("새로운 메뉴를 추가할 수 있다.")
    void create() {
        Menu 메뉴 = MenuTestHelper.메뉴_A;

        mockingMenuGroupRepositoryForCreate(메뉴);
        mockingProductRepositoryForCreate(메뉴);
        mockingPurgomalumClientForCreate(false);
        mockingMenuRepositoryForCreate(메뉴);

        Menu result = menuService.create(메뉴);
        Assertions.assertThat(result.getName()).isEqualTo(메뉴.getName());
    }

    @Test
    @DisplayName("메뉴는 반드시 메뉴 그룹에 포함 되어야 한다.")
    void create_exception_menuGroup() {
        Menu 메뉴_그룹_없는_메뉴 = MenuTestHelper.메뉴_그룹_없는_메뉴;

        Assertions.assertThatThrownBy(
                () -> menuService.create(메뉴_그룹_없는_메뉴)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴는 반드시 가격 정보를 가지고 있어야 한다.")
    void create_exception_price_null() {
        Menu 가격_없는_메뉴 = MenuTestHelper.가격_없는_메뉴;

        Assertions.assertThatThrownBy(
                () -> menuService.create(가격_없는_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴는 반드시 상품 정보를 가지고 있어야 한다.")
    void create_exception_menuProduct_null() {
        Menu 상품_없는_메뉴 = MenuTestHelper.상품_없는_메뉴;

        mockingMenuGroupRepositoryForCreate(상품_없는_메뉴);

        Assertions.assertThatThrownBy(
                () -> menuService.create(상품_없는_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴는 반드시 이름 정보를 가지고 있어야 한다.")
    void create_exception_name_null() {
        Menu 이름_없는_메뉴 = MenuTestHelper.이름_없는_메뉴;

        mockingMenuGroupRepositoryForCreate(이름_없는_메뉴);
        mockingProductRepositoryForCreate(이름_없는_메뉴);

        Assertions.assertThatThrownBy(
                () -> menuService.create(이름_없는_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 추가 시 메뉴의 이름이 부적절한지 검사한다.")
    void create_exception_containsProfanity() {
        Menu 부적절한_이름_메뉴 = MenuTestHelper.부적절한_이름_메뉴;

        mockingMenuGroupRepositoryForCreate(부적절한_이름_메뉴);
        mockingProductRepositoryForCreate(부적절한_이름_메뉴);
        mockingPurgomalumClientForCreate(true);

        Assertions.assertThatThrownBy(
                () -> menuService.create(부적절한_이름_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격은 상품 포함된 상품의 총 가격 보다 클 수 없다.")
    void create_exception_price_difference() {
        Menu 상품_가격보다_큰_메뉴 = MenuTestHelper.상품_가격보다_큰_메뉴;

        mockingMenuGroupRepositoryForCreate(상품_가격보다_큰_메뉴);
        mockingProductRepositoryForCreate(상품_가격보다_큰_메뉴);

        Assertions.assertThatThrownBy(
                () -> menuService.create(상품_가격보다_큰_메뉴)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private void mockingMenuGroupRepositoryForCreate(Menu menu) {
        Mockito.when(menuGroupRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(MenuTestHelper.extractMenuGroupFrom(menu)));
    }

    private void mockingProductRepositoryForCreate(Menu menu) {
        Mockito.when(productRepository.findAllByIdIn(Mockito.any()))
                .thenReturn(MenuTestHelper.extractProductsFrom(menu));
        Mockito.when(productRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(MenuTestHelper.extractProductFrom(menu)));
    }

    private void mockingPurgomalumClientForCreate(boolean result) {
        Mockito.when(purgomalumClient.containsProfanity(Mockito.any()))
                .thenReturn(result);
    }

    private void mockingMenuRepositoryForCreate(Menu menu) {
        Mockito.when(menuRepository.save(Mockito.any()))
                .thenReturn(menu);
    }

    @Test
    @DisplayName("메뉴의 가격을 변경할 수 있다.")
    void changePrice() {
        Menu 메뉴_A = MenuTestHelper.메뉴_A;
        Menu 메뉴_B = MenuTestHelper.메뉴_B;

        mockingMenuRepositoryForChangePrice(메뉴_A);

        menuService.changePrice(메뉴_A.getId(), 메뉴_B);
        Assertions.assertThat(메뉴_A.getPrice()).isEqualTo(메뉴_B.getPrice());
    }

    @Test
    @DisplayName("메뉴의 가격은 해당 상품 총 가격 보다 클 수 없다.")
    void changePrice_exception_price() {
        Menu 메뉴_B = MenuTestHelper.메뉴_B;
        Menu 메뉴_A = MenuTestHelper.메뉴_A;

        mockingMenuRepositoryForChangePrice(메뉴_B);

        Assertions.assertThatThrownBy(
                () -> menuService.changePrice(메뉴_B.getId(), 메뉴_A)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private void mockingMenuRepositoryForChangePrice(Menu menu) {
        Mockito.when(menuRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(menu));
    }
}
