package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroupWithId;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Nested
    class createTest {
        @DisplayName("메뉴를 생성할 수 있다.")
        @Test
        void createSuccessTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));
            Menu createdMenu = menuService.create(menu);

            assertThat(createdMenu.getId()).isNotNull();
        }

        @Test
        @DisplayName("메뉴의 가격이 존재하지 않은 경우 예외가 발생한다.")
        void createFailWhenPriceIsNullTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격이 0보다 작은 경우 예외가 발생한다.")
        void createFailWhenPriceIsNegativeTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(-16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 상품이 존재하지 않은 경우 예외가 발생한다.")
        void createFailWhenMenuProductIsEmptyTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, Collections.emptyList());

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 상품에서 상품이 존재하지 않은 경우 예외가 발생한다.")
        void createFailWhenProductIsEmptyTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            MenuProduct menuProduct = createMenuProduct(UUID.randomUUID(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 상품의 수량이 0보다 작은 경우 예외가 발생한다.")
        void createFailWhenQuantityIsNegativeTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), -1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격이 상품의 가격의 합보다 큰 경우 예외가 발생한다.")
        void createFailWhenPriceIsGreaterThanSumOfProductPriceTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(32000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴에 이름에 비속어가 포함된 경우 예외가 발생한다.")
        @Test
        void createFailWhenNameContainsProfanityTest() {
            given(purgomalumClient.containsProfanity("시발 후라이드치킨")).willReturn(true);
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "시발 후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름이 존재하지 않은 경우에 예외가 발생한다.")
        @Test
        void createFailWhenNameIsNullTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), BigDecimal.valueOf(16000L), true, List.of(menuProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    class changePriceTest {
        @DisplayName("메뉴의 가격을 변경할 수 있다.")
        @Test
        void changePriceSuccessTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));
            menu = menuService.create(menu);

            BigDecimal changedPrice = BigDecimal.valueOf(15000L);
            menu.setPrice(BigDecimal.valueOf(15000L));
            menu = menuService.changePrice(menu.getId(), menu);

            assertThat(menu.getPrice()).isEqualTo(changedPrice);
        }

        @DisplayName("변경할 가격이 0보다 작으면 예외가 발생한다.")
        @Test
        void changePriceFailWhenPriceIsNegativeTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));
            menu = menuService.create(menu);

            UUID menuId = menu.getId();
            menu.setPrice(BigDecimal.valueOf(-15000L));
            Menu changeMenu = menu;

            assertThatThrownBy(() -> menuService.changePrice(menuId, changeMenu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("변경할 가격이 존재하지 않으면 예외가 발생한다.")
        @Test
        void changePriceFailWhenPriceIsNullTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));
            menu = menuService.create(menu);

            UUID menuId = menu.getId();
            menu.setPrice(null);
            Menu changeMenu = menu;

            assertThatThrownBy(() -> menuService.changePrice(menuId, changeMenu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 존재하지 않으면 에러가 발생한다.")
        @Test
        void changePriceFailWhenMenuNotFoundTest() {
            UUID menuId = UUID.randomUUID();
            Menu menu = createMenu(UUID.randomUUID(), "후라이드치킨", BigDecimal.valueOf(16000L), true, Collections.emptyList());

            assertThatThrownBy(() -> menuService.changePrice(menuId, menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴의 가격이 전체 상품의 가격보다 크면 예외가 발생한다.")
        @Test
        void changePriceFailWhenPriceIsGreaterThanSumOfProductPriceTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));
            menu = menuService.create(menu);

            UUID menuId = menu.getId();
            menu.setPrice(BigDecimal.valueOf(32000L));
            Menu changeMenu = menu;

            assertThatThrownBy(() -> menuService.changePrice(menuId, changeMenu))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class displayTest {
        @DisplayName("메뉴를 노출 시킬 수 있다.")
        @Test
        void displaySuccessTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), false, List.of(menuProduct));
            menu = menuService.create(menu);

            UUID menuId = menu.getId();
            menu.setDisplayed(true);
            Menu displayedMenu = menuService.display(menuId);

            assertThat(displayedMenu.isDisplayed()).isTrue();
        }

        @DisplayName("존재하지 않은 메뉴에 대해서 노출을 시도하면 예외가 발생한다.")
        @Test
        void displayFailWhenMenuNotFoundTest() {
            UUID menuId = UUID.randomUUID();

            assertThatThrownBy(() -> menuService.display(menuId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴의 가격이 상품들의 총합보다 크면 예외가 발생한다.")
        @Test
        void displayFailWhenPriceIsGreaterThanSumOfProductPriceTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), false, List.of(menuProduct));
            menu = menuService.create(menu);

            menu.setPrice(BigDecimal.valueOf(32000L));
            menuRepository.save(menu);

            UUID menuId = menu.getId();

            assertThatThrownBy(() -> menuService.display(menuId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class hideTest {
        @DisplayName("메뉴를 비노출 시킬 수 있다.")
        @Test
        void hideSuccessTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));
            menu = menuService.create(menu);

            UUID menuId = menu.getId();
            menu.setDisplayed(false);
            Menu hiddenMenu = menuService.hide(menuId);

            assertThat(hiddenMenu.isDisplayed()).isFalse();
        }

        @DisplayName("존재하지 않은 메뉴에 대해서 비노출을 시도하면 예외가 발생한다.")
        @Test
        void displayFailWhenMenuNotFoundTest() {
            UUID menuId = UUID.randomUUID();

            assertThatThrownBy(() -> menuService.hide(menuId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class findAllTest {
        @DisplayName("모든 메뉴를 조회할 수 있다.")
        @Test
        void findAllSuccessTest() {
            MenuGroup menuGroup = createMenuGroupWithId(UUID.randomUUID(), "메뉴 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);

            Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000L));
            product = productRepository.save(product);

            MenuProduct menuProduct = createMenuProduct(product.getId(), 1);

            Menu menu = createMenu(menuGroup.getId(), "후라이드치킨", BigDecimal.valueOf(16000L), true, List.of(menuProduct));
            menu = menuService.create(menu);

            List<Menu> menus = menuService.findAll();
            List<UUID> menuIds = menus.stream()
                    .map(Menu::getId)
                    .toList();

            assertThat(menus).hasSize(1);
            assertThat(menuIds).contains(menu.getId());
        }
    }
}
