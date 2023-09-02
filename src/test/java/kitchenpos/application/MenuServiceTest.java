package kitchenpos.application;

import kitchenpos.application.fixture.MenuTestFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    MenuService menuService;
    MenuTestFixture menuTestFixture;
    @Mock
    MenuRepository menuRepository;
    @Mock
    MenuGroupRepository menuGroupRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    @BeforeEach
    void setup() {
        this.menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
        this.menuTestFixture = new MenuTestFixture();
    }

    @Nested
    @DisplayName("메뉴 신규 생성 시")
    class Menu_create {

        @DisplayName("가격이 0이상으로 입력되지 않으면 예외를 반환한다.")
        @Test
        void price() {
            Menu menu = menuTestFixture.createMenu(BigDecimal.valueOf(-1));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("가격 0이상");
        }

        @DisplayName("상품이 비어있는 경우 예외를 반환한다.")
        @Test
        void product() {
            Menu menu = menuTestFixture.createMenu(null, BigDecimal.valueOf(100L));

            given(menuGroupRepository.findById(any())).willReturn(Optional.of(menu.getMenuGroup()));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품 비어있음");
        }

        @DisplayName("상품수량이 0 이상이 아닌 경우 예외를 반환한다.")
        @Test
        void product_quantity() {
            MenuProduct menuProduct = menuTestFixture.createMenuProduct(-1);
            Menu menu = menuTestFixture.createMenu(menuProduct, new BigDecimal(1000));

            given(menuGroupRepository.findById(any())).willReturn(Optional.of(menu.getMenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품 수량 0 미만");
        }

        @DisplayName("상품수량*가격이 메뉴 가격보다 비싸지 않으면 예외를 반환한다.")
        @Test
        void menuPrice() {
            MenuProduct menuProduct = menuTestFixture.createMenuProduct(new BigDecimal(100), 1);
            Menu menu = menuTestFixture.createMenu(menuProduct, BigDecimal.valueOf(1000));

            given(menuGroupRepository.findById(any())).willReturn(Optional.of(menu.getMenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
            given(productRepository.findById(any())).willReturn(Optional.of(menuProduct.getProduct()));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("합계오류");
        }

        @DisplayName("상품명에 비속어를 포함하면 예외를 반환한다.")
        @Test
        void productName() {

            MenuProduct menuProduct = menuTestFixture.createMenuProduct("shit", new BigDecimal(10000), 1);
            Menu menu = menuTestFixture.createMenu(menuProduct, BigDecimal.valueOf(1000));

            given(menuGroupRepository.findById(any())).willReturn(Optional.of(menu.getMenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(new Product()));
            given(productRepository.findById(any())).willReturn(Optional.of(menuProduct.getProduct()));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품명오류");
        }
    }

    @Nested
    @DisplayName("가격 변경 시")
    class Price_change {
        @DisplayName("변경할 가격이 0 이상으로 입력되지 않으면 예외를 반환한다.")
        @Test
        void changePrice() {
            Menu menu1 = menuTestFixture.createMenu();
            Menu menu2 = menuTestFixture.createMenu(BigDecimal.valueOf(-1));

            assertThatThrownBy(() -> menuService.changePrice(menu1.getId(), menu2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("가격 변경 실패");
        }

        @DisplayName("메뉴 가격이 내부 모든 메뉴 상품의 (가격*수량) 을 합한 값보다 작지 않으면 예외를 반환한다.")
        @Test
        void changePrice2() {
            MenuProduct menuProduct1 = menuTestFixture.createMenuProduct(BigDecimal.valueOf(100), 1);
            Menu menu1 = menuTestFixture.createMenu(menuProduct1, new BigDecimal(100));

            MenuProduct menuProduct2 = menuTestFixture.createMenuProduct(BigDecimal.valueOf(100), 1);
            Menu menu2 = menuTestFixture.createMenu(menuProduct2, new BigDecimal(1000));

            given(menuRepository.findById(any())).willReturn(Optional.of(menu1));

            assertThatThrownBy(() -> menuService.changePrice(menu1.getId(), menu2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("가격 수량 오류");
        }
    }

    @DisplayName("메뉴를 표시할 때, 메뉴 가격이 내부 모든 메뉴 상품의 (가격*수량) 을 합한 값보다 작지 않으면 예외를 반환한다.")
    @Test
    void display() {
        MenuProduct menuProduct = menuTestFixture.createMenuProduct(BigDecimal.valueOf(100), 1);
        Menu menu = menuTestFixture.createMenu(menuProduct, new BigDecimal(1000));

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThatThrownBy(() -> menuService.display(menu.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("상품 합계 오류");
    }

    @DisplayName("메뉴는 표시하거나 숨길 수 있다.(메뉴가 없는 경우 메뉴를 숨길 수 있다.)")
    @Test
    void displayAndHide() {
        MenuProduct menuProduct = menuTestFixture.createMenuProduct(BigDecimal.valueOf(100), 1);
        Menu menu = menuTestFixture.createMenu(menuProduct, new BigDecimal(1000));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        assertThat(menuService.hide(menu.getId()).isDisplayed()).isFalse();

    }


}
