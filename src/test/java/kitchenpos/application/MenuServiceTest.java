package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.repository.MenuFakeRepository;
import kitchenpos.repository.MenuGroupFakeRepository;
import kitchenpos.repository.ProductFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private MenuService sut;

    private MenuRepository menuRepository;

    private MenuGroupRepository menuGroupRepository;

    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        menuRepository = new MenuFakeRepository();
        menuGroupRepository = new MenuGroupFakeRepository();
        productRepository = new ProductFakeRepository();
        sut = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @Nested
    class 메뉴_등록 {

        @DisplayName("메뉴를 신규 등록한다")
        @Test
        void testCreate() {
            // given
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.create());

            Product product = productRepository.save(ProductFixture.create());
            MenuProduct menuProduct = MenuProductFixture.create(product, 1);

            Menu request = MenuFixture.create(menuGroup, List.of(menuProduct));

            // when
            Menu actual = sut.create(request);

            // then
            Menu expected = menuRepository.findById(actual.getId()).get();

            assertThat(actual.getId()).isEqualTo(expected.getId());
            assertThat(actual.getName()).isEqualTo(expected.getName());
            assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
            assertThat(actual.getMenuGroupId()).isEqualTo(expected.getMenuGroupId());
            assertThat(actual.getMenuGroup().getName()).isEqualTo(expected.getMenuGroup().getName());
            assertThat(actual.getMenuProducts().size()).isEqualTo(expected.getMenuProducts().size());
        }

        @DisplayName("메뉴는 0원 미만의 가격으로 등록할 수 없다")
        @ParameterizedTest
        @ValueSource(ints = {-1_000, -500})
        void testCreateWhenPriceIsNegative(int price) {
            // given
            Menu request = MenuFixture.create(price);

            // when // then
            assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴에 포함된 메뉴 상품이 1개 미만이면 등록할 수 없다")
        @Test
        void testCreateWhenMenuProductsIsEmpty() {
            // given
            MenuGroup menuGroup = MenuGroupFixture.create();
            menuGroupRepository.save(menuGroup);

            Menu request = MenuFixture.create(menuGroup, Collections.emptyList());

            // when // then
            assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴에 포함된 메뉴 상품들의 가격 합이 메뉴 가격보다 작으면 등록할 수 없다")
        @Test
        void testCreateWhenMenuProductsTotalPriceIsLessThanMenuPrice() {
            // given
            Product product = ProductFixture.create(10_000);
            MenuProduct menuProduct = MenuProductFixture.create(product, 2);
            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.create());
            Menu request = MenuFixture.create(22_000, menuGroup, List.of(menuProduct));

            // when // then
            assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class 메뉴_가격_수정 {

        @DisplayName("메뉴의 가격을 수정한다.")
        @Test
        void testChangePrice() {
            // given
            Menu menu = MenuFixture.create();
            int expectedPrice = 5_000;

            menuRepository.save(menu);

            // when
            Menu actual = sut.changePrice(menu.getId(), MenuFixture.create(expectedPrice));

            // then
            assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(expectedPrice));
        }

        @DisplayName("존재하지 않는 메뉴 id로는 메뉴 가격을 수정할 수 없다")
        @Test
        void testChangePriceWhenNotExistMenuId() {
            // given
            Menu menu = MenuFixture.create();
            int expectedPrice = 5_000;

            // when // then
            assertThatThrownBy(() -> sut.changePrice(menu.getId(), MenuFixture.create(expectedPrice)))
                .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("0 미만의 가격으로는 메뉴 가격을 수정할 수 없다")
        @ParameterizedTest
        @ValueSource(ints = {-10_000, -5_000})
        void testChangePriceWhenNegativePrice(int price) {
            // given
            Menu menu = MenuFixture.create();

            // when // then
            assertThatThrownBy(() -> sut.changePrice(menu.getId(), MenuFixture.create(price)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class 메뉴_노출_상태_수정 {

        @DisplayName("특정 메뉴를 노출 처리한다")
        @Test
        void testDisplay() {
            // given
            Menu menu = MenuFixture.create();

            menuRepository.save(menu);

            // when
            Menu actual = sut.display(menu.getId());

            // then
            assertThat(actual.isDisplayed()).isTrue();
        }

        @DisplayName("특정 메뉴를 숨김 처리한다")
        @Test
        void testHide() {
            // given
            Menu menu = MenuFixture.create();

            menuRepository.save(menu);

            // when
            Menu actual = sut.hide(menu.getId());

            // then
            assertThat(actual.isDisplayed()).isFalse();
        }
    }

    @Nested
    class 메뉴_노출 {

        @DisplayName("모든 메뉴를 조회한다")
        @Test
        void testFindAll() {
            // given
            Menu menu1 = MenuFixture.create("testMenu1");
            Menu menu2 = MenuFixture.create("testMenu2");
            menuRepository.save(menu1);
            menuRepository.save(menu2);

            // when
            List<Menu> actual = sut.findAll();

            // then
            assertThat(actual.size()).isEqualTo(2);
            assertThat(actual.get(0).getId()).isEqualTo(menu1.getId());
            assertThat(actual.get(1).getId()).isEqualTo(menu2.getId());
        }
    }
}
