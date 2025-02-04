package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.AdditionalAnswers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenuId;
import static kitchenpos.fixture.MenuFixture.menu;
import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.MenuProductFixture.menuProduct;
import static kitchenpos.fixture.ProductFixture.createProductId;
import static kitchenpos.fixture.ProductFixture.product;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DisplayName("메뉴 서비스 통합 테스트")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Autowired
    @MockBean
    MenuRepository menuRepository;

    @Autowired
    @MockBean
    MenuGroupRepository menuGroupRepository;

    @Autowired
    @MockBean
    ProductRepository productRepository;

    @Autowired
    @MockBean
    PurgomalumClient purgomalumClient;

    @Autowired
    private MenuService menuService;

    @DisplayName("메뉴를 등록 할 때")
    @Nested
    class Create {
        private String menuName = "양념 후라이드 세트";
        private MenuGroup menuGroup = menuGroup();
        private Product firstProduct = product(UUID.randomUUID(), "후라이드 치킨", new BigDecimal("16000"));
        private Product secondProduct = product(UUID.randomUUID(), "양념 치킨", new BigDecimal("16000"));
        private BigDecimal price;
        private MenuProduct firstMenuProduct;
        private MenuProduct secondMenuProduct;
        private Menu menu;

        @BeforeEach
        void setUp() {
            this.menuName = "양념 후라이드 세트";
            this.menuGroup = menuGroup();
            this.firstProduct = product(createProductId(), "후라이드 치킨", new BigDecimal("16000"));
            this.secondProduct = product(createProductId(), "양념 치킨", new BigDecimal("16000"));
            this.price = new BigDecimal("30000");
            this.firstMenuProduct = menuProduct(1L, 1L, firstProduct);
            this.secondMenuProduct = menuProduct(2L, 1L, secondProduct);
            this.menu = menu(null, menuName, price, menuGroup, List.of(firstMenuProduct, secondMenuProduct), true);
        }

        @DisplayName("메뉴를 등록할 수 있습니다.")
        @Test
        void crate() {
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(firstProduct, secondProduct));
            when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
            when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));
            when(purgomalumClient.containsProfanity(menuName)).thenReturn(false);
            when(menuRepository.save(any(Menu.class))).then(AdditionalAnswers.returnsFirstArg());

            final Menu actual = menuService.create(menu);
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual.getId()).isNotNull(),
                    () -> assertThat(actual.getName()).isEqualTo(menuName),
                    () -> assertThat(actual.getPrice()).isEqualByComparingTo(new BigDecimal("30000")),
                    () -> assertThat(actual.getMenuGroup()).isEqualTo(menuGroup),
                    () -> assertThat(actual.getMenuProducts()).hasSize(2),
                    () -> assertThat(actual.isDisplayed()).isTrue()
            );
        }

        @DisplayName("메뉴 가격은 0원 이상이어야 합니다.")
        @ParameterizedTest(name = "입력값 `{0}`")
        @ValueSource(strings = {"-1", "-1000", "-100000"})
        void createWithNegativePrice(String price) {
            final Menu menu = menu(null, menuName, new BigDecimal(price),
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), true
            );
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 그룹이 존재하지 않으면 메뉴를 등록할 수 없습니다.")
        @Test
        void createWithNotExistsMenuGroup() {
            final Menu menu = menu(null, menuName, price,
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), true
            );
            when(menuGroupRepository.findById(menu.getMenuGroupId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴 상품이 없거나 비어있으면 메뉴를 등록할 수 없습니다.")
        @ParameterizedTest(name = "입력값 `{0}`")
        @NullAndEmptySource
        void createWithEmptyMenuProducts(final List<MenuProduct> menuProducts) {
            final Menu menu = menu(null, menuName, price,
                    menuGroup, menuProducts, true
            );
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 상품의 갯수와 상품의 갯수가 다르면 메뉴를 등록할 수 없습니다.")
        @Test
        void createWithDifferentMenuProductSize() {
            final Menu menu = menu(null, menuName, price,
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), true
            );
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of());

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("매뉴 상품의 수량이 0보다 작으면 메뉴를 등록할 수 없습니다.")
        @ParameterizedTest(name = "수량 : `{0}`")
        @CsvSource(value = {"1:-1", "-1:1"}, delimiter = ':')
        void createWithNegativeMenuProductQuantity(final long firstQuantity, final long secondQuantity) {
            final Menu menu = menu(null, menuName, price,
                    menuGroup, List.of(
                            menuProduct(1L, firstQuantity, firstProduct),
                            menuProduct(2L, secondQuantity, secondProduct)
                    ), true
            );
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(firstProduct, secondProduct));
            when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
            when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 가격이 메뉴 상품의 가격 합보다 크면 메뉴를 등록할 수 없습니다.")
        @Test
        void createWithPriceLessThanSumOfMenuProductPrice() {
            final Menu menu = menu(null, menuName, new BigDecimal("32010"),
                    menuGroup, List.of(
                            menuProduct(1L, 1L, firstProduct),
                            menuProduct(2L, 1L, secondProduct)
                    ), true
            );
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(firstProduct, secondProduct));
            when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
            when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 이름이 없거나 비어있으면 메뉴를 등록할 수 없습니다.")
        @ParameterizedTest(name = "메뉴 이름 : `{0}`")
        @NullAndEmptySource
        void createWithEmptyOrBlankMenuName(final String name) {
            final Menu menu = menu(null, name, price,
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), true
            );
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(firstProduct, secondProduct));
            when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
            when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 이름이 욕설이 포함되어 있으면 메뉴를 등록할 수 없습니다.")
        @ParameterizedTest(name = "입력값 `{0}`")
        @ValueSource(strings = {"비속어", "욕설", "그XX"})
        void createWithEmptyOrProfanityMenuName(final String name) {
            final Menu menu = menu(null, name, price,
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), true
            );
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(firstProduct, secondProduct));
            when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
            when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));
            when(purgomalumClient.containsProfanity(name)).thenReturn(true);

            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("메뉴 가격을 수정할 때")
    @Nested
    class ChangePrice {

        private MenuGroup menuGroup;
        private BigDecimal price;
        private MenuProduct firstMenuProduct;
        private MenuProduct secondMenuProduct;
        private UUID menuId;
        private String menuName;
        private Menu menu;

        @BeforeEach
        void setUp() {
            this.price = new BigDecimal("30000");
            this.menuId = createMenuId();
            this.menuGroup = menuGroup();
            this.firstMenuProduct = menuProduct(1L, 1L, product(createProductId(), "후라이드 치킨", new BigDecimal("16000")));
            this.secondMenuProduct = menuProduct(2L, 1L, product(createProductId(), "양념 치킨", new BigDecimal("16000")));
            this.menuName = "양념 후라이드 세트";
            this.menu = menu(menuId, menuName, price, menuGroup, List.of(firstMenuProduct, secondMenuProduct), true);
        }

        @DisplayName("메뉴 가격을 수정할 수 있습니다.")
        @ParameterizedTest(name = "메뉴 가격 : `{0}`")
        @ValueSource(strings = {"31990", "31000", "32000"})
        void changePrice(final String price) {
            final BigDecimal changePrice = new BigDecimal(price);
            final Menu changedMenu = menu(menuId, menuName, changePrice,
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), true
            );
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

            final Menu actual = menuService.changePrice(menu.getId(), changedMenu);
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual.getPrice()).isEqualByComparingTo(changePrice)
            );
        }

        @DisplayName("메뉴 가격이 0원 미만이면 메뉴 가격을 수정할 수 없습니다.")
        @ParameterizedTest(name = "메뉴 가격 : `{0}`")
        @ValueSource(strings = {"-1", "-1000", "-10000"})
        void changePriceWithNegativePrice(final String price) {
            final Menu changedMenu = menu(menuId, menuName, new BigDecimal(price),
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), true
            );
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changedMenu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 가격이 없으면 메뉴 가격을 수정할 수 없습니다.")
        @ParameterizedTest(name = "메뉴 가격 : `{0}`")
        @NullSource
        void changePriceWithNullPrice(final BigDecimal nullPrice) {
            final Menu changedMenu = menu(menuId, menuName, nullPrice,
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), true
            );
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changedMenu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 존재하지 않으면 메뉴 가격을 수정할 수 없습니다.")
        @Test
        void changePriceWithNotExistsMenu() {
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴의 가격이 메뉴 상품의 가격 합보다 크면 메뉴 가격을 수정할 수 없습니다.")
        @ParameterizedTest(name = "메뉴 가격 : `{0}`")
        @ValueSource(strings = {"32010", "33000", "34000"})
        void changePriceWithPriceLessThanSumOfMenuProductPrice(final String price) {
            final Menu changedMenu = menu(menuId, menuName, new BigDecimal(price),
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), true
            );
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changedMenu))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("메뉴를 노출시킬 때")
    @Nested
    class Display {
        private MenuGroup menuGroup;
        private BigDecimal price;
        private MenuProduct firstMenuProduct;
        private MenuProduct secondMenuProduct;
        private UUID menuId;
        private String menuName;
        private Menu menu;

        @BeforeEach
        void setUp() {
            this.price = new BigDecimal("30000");
            this.menuId = UUID.randomUUID();
            this.menuGroup = menuGroup();
            this.firstMenuProduct = menuProduct(1L, 1L, product(UUID.randomUUID(), "후라이드 치킨", new BigDecimal("16000")));
            this.secondMenuProduct = menuProduct(2L, 1L, product(UUID.randomUUID(), "양념 치킨", new BigDecimal("16000")));
            this.menuName = "양념 후라이드 세트";
            this.menu = menu(menuId, menuName, price, menuGroup, List.of(firstMenuProduct, secondMenuProduct), true);
        }

        @DisplayName("메뉴를 노출할 수 있습니다.")
        @Test
        void display() {
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

            final Menu actual = menuService.display(menu.getId());
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual.getId()).isEqualTo(menu.getId()),
                    () -> assertThat(actual.isDisplayed()).isTrue()
            );
        }

        @DisplayName("메뉴의 가격이 메뉴 상품의 가격 합보다 크면 메뉴를 노출할 수 없습니다.")
        @ParameterizedTest(name = "메뉴 가격 : `{0}`")
        @ValueSource(strings = {"32010", "33000", "34000"})
        void displayWithPriceLessThanSumOfMenuProductPrice(final String price) {
            final Menu menu = menu(menuId, menuName, new BigDecimal(price),
                    menuGroup, List.of(firstMenuProduct, secondMenuProduct), false
            );
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

            assertThatThrownBy(() -> menuService.display(menu.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("메뉴가 존재하지 않으면 메뉴를 노출할 수 없습니다.")
        @Test
        void displayWithNotExistsMenu() {
            when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> menuService.display(menuId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("메뉴가 노출 되고 있을 때")
    @Nested
    class Hide {

        private MenuGroup menuGroup;
        private BigDecimal price;
        private MenuProduct firstMenuProduct;
        private MenuProduct secondMenuProduct;
        private UUID menuId;
        private String menuName;
        private Menu menu;

        @BeforeEach
        void setUp() {
            this.price = new BigDecimal("30000");
            this.menuId = UUID.randomUUID();
            this.menuGroup = menuGroup();
            this.firstMenuProduct = menuProduct(1L, 1L, product(UUID.randomUUID(), "후라이드 치킨", new BigDecimal("16000")));
            this.secondMenuProduct = menuProduct(2L, 1L, product(UUID.randomUUID(), "양념 치킨", new BigDecimal("16000")));
            this.menuName = "양념 후라이드 세트";
            this.menu = menu(menuId, menuName, price, menuGroup, List.of(firstMenuProduct, secondMenuProduct), true);
        }

        @DisplayName("메뉴를 숨길 수 있습니다.")
        @Test
        void hide() {
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

            final Menu actual = menuService.hide(menu.getId());
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual.getId()).isEqualTo(menu.getId()),
                    () -> assertThat(actual.isDisplayed()).isFalse()
            );
        }

        @DisplayName("메뉴가 존재하지 않으면 메뉴를 숨길 수 없습니다.")
        @Test
        void hideWithNotExistsMenu() {
            when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> menuService.hide(menuId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
