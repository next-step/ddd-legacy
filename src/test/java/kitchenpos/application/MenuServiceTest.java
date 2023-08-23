package kitchenpos.application;

import kitchenpos.ApplicationTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("메뉴")
class MenuServiceTest extends ApplicationTest {

    @Mock
    private PurgomalumClient purgomalumClient;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @InjectMocks
    private MenuService menuService;

    private Product product_1000;
    private Product product_2000;
    private MenuGroup menuGroup;
    private List<MenuProduct> menuProducts;

    @BeforeEach
    void setup() {
        product_1000 = ProductFixture.create(BigDecimal.valueOf(1000));
        product_2000 = ProductFixture.create(BigDecimal.valueOf(2000));
        menuGroup = MenuGroupFixture.createDefault();
        menuProducts = MenuProductFixture.createDefaultsWithProduct(product_1000, product_2000);
    }


    @Nested
    @DisplayName("이름")
    class Name {
        @DisplayName("[예외] 메뉴 이름은 공백일 수 없다")
        @ParameterizedTest
        @NullSource
        void nameTest1(String name) {
            //when
            Menu menu = MenuFixture.create(
                    name
                    , BigDecimal.valueOf(10000)
                    , true
                    , menuGroup
                    , menuProducts);

            setUpMockDefault();

            //then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 메뉴 이름은 비속어면 안된다.")
        @Test
        void name_test_2() {
            //given
            Menu menu = MenuFixture.create(
                    "메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , menuProducts);

            setUpMockDefault();

            //when
            when(purgomalumClient.containsProfanity(any())).thenReturn(true);
            //then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    @DisplayName("등록")
    class Create {
        @DisplayName("[성공] 메뉴를 등록한다.")
        @Test
        void createTest1() {
            //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , menuProducts);

            setUpMockDefault();

            when(menuRepository.save(any())).thenReturn(menu);
            //when
            Menu created = menuService.create(menu);
            //then
            assertAll(
                    () -> assertThat(created).isEqualTo(menu)
                    , () -> assertThat(created.getPrice()).isEqualTo(menu.getPrice())
                    , () -> assertThat(created.getName()).isEqualTo(menu.getName())
                    , () -> assertThat(created.getMenuProducts()).containsAll(menuProducts)
                    , () -> assertThat(created.isDisplayed()).isTrue()
            );
        }



        @DisplayName("[예외] 등록된 메뉴그룹만 지정할 수 있다.")
        @Test
        void createTest2() {
            //given
            MenuGroup newMenuGroup = MenuGroupFixture.createDefault();
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , newMenuGroup
                    , menuProducts);

            when(menuGroupRepository.findById(any()))
                    .thenReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(NoSuchElementException.class);

        }

        @DisplayName("[예외] 등록된 상품만 지정할 수 있다.")
        @Test
        void createTest3() {
            //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , menuProducts);

            when(menuGroupRepository.findById(any()))
                    .thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(any()))
                    .thenReturn(List.of(product_1000, product_2000));
            when(productRepository.findById(any()))
                    .thenReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 상품은 1개 이상이어야 한다. ")
        @Test
        void createTest4() {
        //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , Collections.emptyList());

            when(menuGroupRepository.findById(any()))
                    .thenReturn(Optional.of(menuGroup));
            //then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 상품의 수량은 0개 이상이어야 한다.")
        @Test
        void createTest5() {
            //given
            MenuProduct menuProduct = MenuProductFixture.create(product_1000, -1);
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , List.of(menuProduct));

            //given
            when(menuGroupRepository.findById(any()))
                    .thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(any()))
                    .thenReturn(List.of(product_1000));

            //then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("보이기")
    class Display {
        @DisplayName("[성공] 메뉴를 보인다.")
        @Test
        void displayTest1() {
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(900)
                    , false
                    , menuGroup
                    , menuProducts);
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));
            //when
            Menu display = menuService.display(menu.getId());
            //then
            assertThat(display.isDisplayed()).isTrue();


        }

        @DisplayName("[예외] 메뉴의 가격은 각 구성상품의 수량*가격보다 작아야 한다.")
        @Test
        void displayTest2() {
            //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1200)
                    , false
                    , menuGroup
                    , menuProducts);
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));
            //then
            assertThatThrownBy(()->menuService.display(menu.getId()))
                    .isInstanceOf(IllegalStateException.class);

        }

        @DisplayName("[예외] 등록된 메뉴만 보일 수 있다.")
        @Test
        void displayTest3() {
            //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(2000)
                    , false
                    , menuGroup
                    , menuProducts);
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.empty());
            //then
            assertThatThrownBy(()->menuService.display(menu.getId()))
                    .isInstanceOf(NoSuchElementException.class);

        }
    }

    @Nested
    @DisplayName("숨기기")
    class Hide {
        @DisplayName("[성공] 메뉴를 숨긴다.")
        @Test
        void hideTest1() {
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , menuProducts);
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));
            //when
            Menu display = menuService.hide(menu.getId());
            //then
            assertThat(display.isDisplayed()).isFalse();
        }

        @DisplayName("[예외] 등록된 메뉴만 숨길 수 있다.")
        @Test
        void hideTest2() {
            //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , menuProducts);
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.empty());
            //then
            assertThatThrownBy(()->menuService.hide(menu.getId()))
                    .isInstanceOf(NoSuchElementException.class);

        }
    }

    @Nested
    @DisplayName("가격 바꾸기")
    class ChangePrice {
        @DisplayName("[성공] 가격을 바꾼다.")
        @Test
        void changPriceTest1() {
            //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , menuProducts);
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));
            //when
            BigDecimal 바꿀가격 = BigDecimal.valueOf(900);
            menu.setPrice(바꿀가격);
            Menu changePrice = menuService.changePrice(menu.getId(), menu);
            //then
            assertThat(changePrice.getPrice()).isEqualTo(바꿀가격);
        }

        @DisplayName("[예외] 가격은 없을 수 없다.")
        @Test
        void changPriceTest2() {
            //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , menuProducts);
            //when
            menu.setPrice(null);
            //then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 가격은 0원 이상이다")
        @ParameterizedTest
        @ValueSource(longs = {-1, -10000})
        void changPrice_test_3(long price) {
            //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , menuProducts);
            //when
            menu.setPrice(BigDecimal.valueOf(price));
            //then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 메뉴의 가격은 각 구성상품의 수량*가격보다 작아야 한다.")
        @Test
        void changPriceTest4() {
            //given
            Menu menu = MenuFixture.create("메뉴"
                    , BigDecimal.valueOf(1500)
                    , true
                    , menuGroup
                    , menuProducts);
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));
            //when
            BigDecimal 바꿀가격 = BigDecimal.valueOf(1200);
            menu.setPrice(바꿀가격);
            //then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    private void setUpMockDefault() {
        when(menuGroupRepository.findById(menuGroup.getId()))
                .thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(any()))
                .thenReturn(List.of(product_1000, product_2000));
        when(productRepository.findById(product_1000.getId()))
                .thenReturn(Optional.of(product_1000));
        when(productRepository.findById(product_2000.getId()))
                .thenReturn(Optional.of(product_1000));
    }
}
