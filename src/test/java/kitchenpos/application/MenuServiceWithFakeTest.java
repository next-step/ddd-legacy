package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.ProfanityClient;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.fixture.fake.FakeProfanityClient;
import kitchenpos.fixture.fake.InMemoryMenuGroupRepository;
import kitchenpos.fixture.fake.InMemoryMenuRepository;
import kitchenpos.fixture.fake.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuServiceWithFakeTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private ProfanityClient profanityClient;
    private MenuService menuService;

    private Product chicken;
    private MenuGroup recommendGroup;
    private Menu friedChickenMenu;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        profanityClient = new FakeProfanityClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);

        chicken = ProductFixture.createDefault();
        productRepository.save(chicken);

        recommendGroup = MenuGroupFixture.create("추천메뉴");
        menuGroupRepository.save(recommendGroup);

        friedChickenMenu = MenuFixture.create(
                "후라이드 치킨",
                BigDecimal.valueOf(15_000L),
                true,
                recommendGroup,
                List.of(MenuProductFixture.of(chicken))
        );
        menuRepository.save(friedChickenMenu);
    }

    @DisplayName("메뉴 등록")
    @Nested
    class CreateTest {

        @DisplayName("등록 성공")
        @Test
        void createdMenu() {
            // given
            final Menu request = MenuFixture.createRequest(
                    "후라이드 치킨",
                    BigDecimal.valueOf(15_000L),
                    true,
                    recommendGroup.getId(),
                    List.of(MenuProductFixture.createRequest(chicken.getId(), 1L))
            );

            // when
            final Menu result = menuService.create(request);

            // then
            assertAll(() -> {
                assertThat(result.getId()).isNotNull();
                assertThat(result.getName()).isEqualTo("후라이드 치킨");
                assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(15_000));
                assertThat(result.getMenuGroup()).isNotNull();
                assertThat(result.getMenuProducts().size()).isEqualTo(1);
            });
        }

        @DisplayName("가격은 0원 이상이여야 한다.")
        @Test
        void negative_price() {
            // given
            final Menu request = MenuFixture.createRequest(
                    "후라이드 치킨",
                    BigDecimal.valueOf(-15_000L),
                    true,
                    recommendGroup.getId(),
                    List.of(MenuProductFixture.createRequest(chicken.getId(), 1L))
            );

            // then
            assertThatThrownBy(() -> menuService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴그룹에 속해야 한다.")
        @Test
        void not_contain_menuGroup() {
            // given
            final Menu request = MenuFixture.createRequest(
                    "후라이드 치킨",
                    BigDecimal.valueOf(15_000L),
                    true,
                    MenuGroupFixture.createDefault().getId(),
                    List.of(MenuProductFixture.createRequest(chicken.getId(), 1L))
            );

            // then
            assertThatThrownBy(() -> menuService.create(request)).isInstanceOf(NoSuchElementException.class);
        }

        @ParameterizedTest(name = "구성품목은 비어있을 수 없다. menuProducts={0}")
        @NullAndEmptySource
        void empty_or_null_menuProduct(List<MenuProduct> menuProducts) {
            // given
            final Menu request = MenuFixture.createRequest(
                    "후라이드 치킨",
                    BigDecimal.valueOf(15_000L),
                    true,
                    recommendGroup.getId(),
                    menuProducts
            );

            // then
            assertThatThrownBy(() -> menuService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("구성품목은 등록된 제품만 가능하다.")
        @Test
        void not_created_product() {
            // given
            final Menu request = MenuFixture.createRequest(
                    "후라이드 치킨",
                    BigDecimal.valueOf(15_000),
                    true,
                    recommendGroup.getId(),
                    List.of(MenuProductFixture.createRequest(1L))
            );

            // then
            assertThatThrownBy(() -> menuService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("구성품목의 개수는 0개 이상이여야 한다.")
        @Test
        void negative_menuProduct_quantity() {
            // given
            final Menu request = MenuFixture.createRequest(
                    "후라이드 치킨",
                    BigDecimal.valueOf(15_000),
                    true,
                    recommendGroup.getId(),
                    List.of(MenuProductFixture.createRequest(-1L))
            );

            // then
            assertThatThrownBy(() -> menuService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("가격은 (구성품목의 가격 * 개수)의 총합보다 클 수 없다.")
        @Test
        void over_than_product_price() {
            // given
            final Menu request = MenuFixture.createRequest(
                    "후라이드 치킨",
                    BigDecimal.valueOf(30_000),
                    true,
                    recommendGroup.getId(),
                    List.of(MenuProductFixture.createRequest(chicken.getId(), 1L))
            );

            // then
            assertThatThrownBy(() -> menuService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 비어있을 수 없다.")
        @Test
        void null_name() {
            // given
            final Menu request = MenuFixture.createRequest(
                    null,
                    BigDecimal.valueOf(15_000),
                    true,
                    recommendGroup.getId(),
                    List.of(MenuProductFixture.createRequest(chicken.getId(), 1L))
            );

            // then
            assertThatThrownBy(() -> menuService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 욕설, 외설 및 기타 원치 않는 용어에 해당할 수 없다.")
        @Test
        void invalid_name() {
            // given
            final Menu request = MenuFixture.createRequest(
                    "비속어",
                    BigDecimal.valueOf(15_000),
                    true,
                    recommendGroup.getId(),
                    List.of(MenuProductFixture.createRequest(chicken.getId(), 1L))
            );

            // then
            assertThatThrownBy(() -> menuService.create(request)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("메뉴의 가격 수정")
    @Nested
    class ChangePriceTest {

        @DisplayName("수정 성공")
        @Test
        void changed_price() {
            // given
            final Menu request = MenuFixture.createPriceRequest(BigDecimal.valueOf(10_000L));

            // when
            final Menu result = menuService.changePrice(friedChickenMenu.getId(), request);

            // then
            assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(10_000L));
        }


        @DisplayName("수정하려는 가격은 null 이면 안된다.")
        @Test
        void null_price() {
            // given
            final Menu request = MenuFixture.createPriceRequest(null);

            // then
            assertThatThrownBy(() -> menuService.changePrice(friedChickenMenu.getId(), request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("수정하려는 가격은 0원 이상이여야 한다.")
        @Test
        void negative_price() {
            // given
            final Menu request = MenuFixture.createPriceRequest(BigDecimal.valueOf(-10_000L));

            // then
            assertThatThrownBy(() -> menuService.changePrice(friedChickenMenu.getId(), request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("수정하려는 가격은 (구성품목의 가격 * 개수)의 총합보다 클 수 없다.")
        @Test
        void over_than_product_price() {
            // given
            final Menu request = MenuFixture.createPriceRequest(BigDecimal.valueOf(30_000));

            // then
            assertThatThrownBy(() -> menuService.changePrice(friedChickenMenu.getId(), request)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("메뉴 개시")
    @Nested
    class displayTest {

        @DisplayName("개시 성공")
        @Test
        void displayed() {
            // given
            final Menu hiddenChicken = MenuFixture.create(
                    "후라이드 치킨",
                    BigDecimal.valueOf(15_000L),
                    false,
                    recommendGroup,
                    List.of(MenuProductFixture.of(chicken))
            );
            menuRepository.save(hiddenChicken);

            // when
            Menu result = menuService.display(hiddenChicken.getId());

            // then
            assertThat(result.isDisplayed()).isTrue();
        }

        @DisplayName("메뉴의 가격이 (구성품목의 가격 x 개수)의 총합보다 클 수 없다.")
        @Test
        void over_than_product_price() {
            // given
            final Menu hiddenChicken = MenuFixture.create(
                    "후라이드 치킨",
                    BigDecimal.valueOf(30_000),
                    false,
                    MenuGroupFixture.createDefault(),
                    List.of(MenuProductFixture.create(
                            ProductFixture.create("후라이드 치킨", BigDecimal.valueOf(10_000)),
                            1L
                    ))
            );
            menuRepository.save(hiddenChicken);

            // then
            assertThatThrownBy(() -> menuService.display(hiddenChicken.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("메뉴를 숨긴다.")
    @Test
    void hideMenu() {
        // when
        Menu result = menuService.hide(friedChickenMenu.getId());

        // then
        assertThat(result.isDisplayed()).isFalse();
    }
}
