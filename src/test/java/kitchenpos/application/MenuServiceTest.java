package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.MenuProductFixture.*;
import static kitchenpos.fixture.ProductFixture.product;
import static org.assertj.core.api.Assertions.*;


@Transactional
@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Nested
    class 메뉴_생성_경우 {
        @Test
        void 유효한_메뉴_요청이면_정상적으로_생성된다() {
            // given
            Menu request = menu(DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE, createMenuGroup(), List.of(createMenuProduct(1L)), DEFAULT_DISPLAYED);

            // when
            Menu response = menuService.create(request);

            // then
            assertThat(response.getName()).isEqualTo(request.getName());
            assertThat(response.getPrice()).isEqualTo(request.getPrice());
            assertThat(response.getMenuGroup().getId()).isEqualTo(request.getMenuGroupId());
            assertThat(response.getMenuProducts()).hasSize(1);
        }

        @Test
        void 메뉴_가격이_구성_상품_총_합보다_높으면_예외가_발생한다() {
            // given
            Menu request = menu(DEFAULT_MENU_NAME, 12_000L, createMenuGroup(), List.of(createMenuProduct(1L)), DEFAULT_DISPLAYED);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @Test
        void 이름이_NULL이면_예외가_발생한다() {
            // given
            Menu request = menu(null, DEFAULT_MENU_PRICE, createMenuGroup(), List.of(createMenuProduct(1L)), DEFAULT_DISPLAYED);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @ParameterizedTest(name = "입력 값 `{0}`")
        @ValueSource(strings = {"bitch", "shit"})
        void 이름의_욕설이_포함되면_예외가_발생한다(final String name) {
            // given
            Menu request = menu(name, DEFAULT_MENU_PRICE, createMenuGroup(), List.of(createMenuProduct(1L)), DEFAULT_DISPLAYED);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @Test
        void 메뉴_상품이_NULL이면_예외가_발생한다() {
            // given
            Menu request = menu(DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE, createMenuGroup(), null, DEFAULT_DISPLAYED);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @Test
        void 메뉴_상품이_비어있다면_예외가_발생한다() {
            // given
            Menu request = menu(DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE, createMenuGroup(), Collections.emptyList(), DEFAULT_DISPLAYED);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @Test
        void 구성_상품의_수량이_음수이면_예외가_발생한다() {
            // given
            Menu request = menu(DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE, createMenuGroup(), List.of(createMenuProduct(-1L)), DEFAULT_DISPLAYED);

            // when & then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }
    }

    @Nested
    class 메뉴_표시_경우 {
        @Test
        void 구성_상품_총_합이_메뉴_가격_이상이면_메뉴를_표시할_수_있다() {
            // given
            Menu request = menuService.create(menu(DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE, createMenuGroup(), List.of(createMenuProduct(1L)), DEFAULT_DISPLAYED));

            // when
            Menu response = menuService.display(request.getId());

            // then
            assertThat(response.isDisplayed()).isTrue();
        }

        @Test
        void 구성_상품_총_합이_메뉴_가격보다_낮으면_메뉴_표시_시_예외가_발생한다() {
            // given
            Menu request = menuRepository.save(menu(createMenuId(), DEFAULT_MENU_NAME, BigDecimal.valueOf(12_000L), createMenuGroup(), List.of(createMenuProduct(1L)), false));

            // when & then
            assertThatIllegalStateException()
                    .isThrownBy(() -> menuService.display(request.getId()));
        }
    }

    @Nested
    class 메뉴_숨김_경우 {
        @Test
        void 메뉴_숨김_처리는_정상적으로_수행된다() {
            // given
            Menu request = menuService.create(menu(DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE, createMenuGroup(), List.of(createMenuProduct(1L)), DEFAULT_DISPLAYED));

            // when
            Menu response = menuService.hide(request.getId());

            // then
            assertThat(response.isDisplayed()).isFalse();
        }
    }

    @Nested
    class 메뉴_조회_경우 {
        @Test
        void 전체_메뉴를_조회하면_생성된_모든_메뉴가_반환된다() {
            // given
            Product product1 = product("후라이드 치킨", 14_000);
            Product product2 = product("양념 치킨", 14_000);
            productRepository.saveAll(List.of(product1, product2, product2));

            MenuProduct menuProduct1 = menuProduct(seq(), DEFALUT_QUANTITY, product1);
            MenuProduct menuProduct2 = menuProduct(seq(), DEFALUT_QUANTITY, product2);

            Menu request1 = menu(DEFAULT_MENU_NAME, DEFAULT_MENU_PRICE, createMenuGroup(), List.of(createMenuProduct(1L)), DEFAULT_DISPLAYED);
            menuService.create(request1);

            Menu request2 = menu(SECONDARY_MENU_NAME, SECONDARY_MENU_PRICE, createMenuGroup(), List.of(menuProduct1, menuProduct2), DEFAULT_DISPLAYED);
            menuService.create(request2);

            // when
            List<Menu> response = menuService.findAll();

            // then
            assertThat(response).hasSize(2);
        }
    }

    private MenuGroup createMenuGroup() {
        return menuGroupRepository.save(menuGroup());
    }

    private MenuProduct createMenuProduct(final long quantity) {
        return menuProduct(seq(), quantity, productRepository.save(product()));
    }

}
