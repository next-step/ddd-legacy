package kitchenpos.application;

import kitchenpos.domain.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MenuServiceTest {
    @Autowired
    private MenuService menuService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private MenuRepository menuRepository;

    @DisplayName("메뉴 등록하기")
    @Nested
    class MenuRegisterTest {

        private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.randomUUID();
        private static final UUID 치킨류_MENU_GROUP_UUID = UUID.randomUUID();
        private static final String 후라이드치킨_PRODUCT_NAME = "후라이드치킨";
        private static final String 치킨류_MENU_GROUP_NAME = "치킨류";
        private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);

        @BeforeEach
        void setup() {
            Product product = new Product();
            product.setId(후라이드치킨_PRODUCT_UUID);
            product.setName(후라이드치킨_PRODUCT_NAME);
            product.setPrice(후라이드치킨_DEFAULT_PRICE);
            productRepository.save(product);

            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(치킨류_MENU_GROUP_UUID);
            menuGroup.setName(치킨류_MENU_GROUP_NAME);
            menuGroupRepository.save(menuGroup);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴를 정상적으로 등록한다")
        @Test
        void create_menu_successfully() {
            // given
            Menu request = new Menu();
            request.setName("후라이드치킨");
            request.setPrice(new BigDecimal(16000));
            request.setMenuGroupId(치킨류_MENU_GROUP_UUID);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(후라이드치킨_PRODUCT_UUID);
            menuProduct.setQuantity(2);
            List<MenuProduct> menuProducts = List.of(menuProduct);
            request.setMenuProducts(menuProducts);
            request.setDisplayed(true);

            // when
            Menu menu = menuService.create(request);

            // then
            assertAll(
                    () -> assertThat(menu.getId()).isNotNull(),
                    () -> assertThat(menu.getName()).isEqualTo(request.getName()),
                    () -> assertThat(menu.getPrice()).isEqualTo(request.getPrice()),
                    () -> assertThat(menu.isDisplayed()).isEqualTo(request.isDisplayed()),
                    () -> assertThat(menu.getMenuGroup().getId()).isEqualTo(request.getMenuGroupId()),
                    () -> assertThat(menu.getMenuProducts()).hasSize(menuProducts.size())
            );
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("이름에는 비속어와 같이 부적절한 단어는 사용할 수 없다")
        @Test
        void registration_menu_with_profanity() {
            // given
            Menu request = new Menu();
            request.setName("holy shit 후라이드치킨");
            request.setPrice(new BigDecimal(16000));
            request.setMenuGroupId(치킨류_MENU_GROUP_UUID);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(후라이드치킨_PRODUCT_UUID);
            menuProduct.setQuantity(2);
            request.setMenuProducts(List.of(menuProduct));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("이름은 반드시 입력해야 합니다")
        @Test
        void name_must_be_input() {
            // given
            Menu request = new Menu();
            request.setPrice(new BigDecimal(16000));
            request.setMenuGroupId(치킨류_MENU_GROUP_UUID);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(후라이드치킨_PRODUCT_UUID);
            menuProduct.setQuantity(2);
            request.setMenuProducts(List.of(menuProduct));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴는 적어도 하나 이상의 상품을 포함해야 한다")
        @Test
        void create_menu_without_products() {
            // given
            Menu request = new Menu();
            request.setName("후라이드치킨");
            request.setPrice(new BigDecimal(16000));
            request.setMenuGroupId(치킨류_MENU_GROUP_UUID);
            request.setMenuProducts(Collections.emptyList());

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("상품의 갯수는 0개 이상 입력해야 한다")
        @Test
        void product_quantity_must_be_positive() {
            // given
            Menu request = new Menu();
            request.setName("후라이드치킨");
            request.setPrice(new BigDecimal(16000));
            request.setMenuGroupId(치킨류_MENU_GROUP_UUID);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(후라이드치킨_PRODUCT_UUID);
            menuProduct.setQuantity(-1);
            request.setMenuProducts(List.of(menuProduct));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴 가격은 0 이상이어야 한다")
        @Test
        void create_menu_with_invalid_price() {
            // given
            Menu request = new Menu();
            request.setName("후라이드치킨");
            request.setPrice(new BigDecimal(-100));
            request.setMenuGroupId(치킨류_MENU_GROUP_UUID);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(후라이드치킨_PRODUCT_UUID);
            menuProduct.setQuantity(1);
            request.setMenuProducts(List.of(menuProduct));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴 가격은 상품 가격 총합을 초과할 수 없다")
        @Test
        void create_menu_with_price_exceeding_product_total() {
            // given
            Menu request = new Menu();
            request.setName("후라이드치킨");
            request.setPrice(new BigDecimal(50000));
            request.setMenuGroupId(치킨류_MENU_GROUP_UUID);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(후라이드치킨_PRODUCT_UUID);
            menuProduct.setQuantity(1);
            request.setMenuProducts(List.of(menuProduct));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴는 하나의 메뉴그룹에 속한다")
        @Test
        void create_menu_without_menu_group() {
            // given
            Menu request = new Menu();
            request.setName("후라이드치킨");
            request.setPrice(new BigDecimal(16000));
            UUID noExistsGroupId = UUID.randomUUID();
            request.setMenuGroupId(noExistsGroupId);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(후라이드치킨_PRODUCT_UUID);
            menuProduct.setQuantity(1);
            request.setMenuProducts(List.of(menuProduct));

            // when
            Executable executable = () -> menuService.create(request);

            // then
            assertThrows(NoSuchElementException.class, executable);
        }
    }

    @DisplayName("메뉴 가격 변경하기")
    @Nested
    class ChangeMenuPriceTest {

        private static final UUID MENU_UUID = UUID.randomUUID();
        private static final UUID PRODUCT_UUID = UUID.randomUUID();
        private static final UUID MENU_GROUP_UUID = UUID.randomUUID();

        @BeforeEach
        void setup() {
            Product product = new Product();
            product.setId(PRODUCT_UUID);
            product.setName("양념치킨");
            product.setPrice(new BigDecimal(20000));
            productRepository.save(product);

            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(MENU_GROUP_UUID);
            menuGroup.setName("치킨류");
            menuGroupRepository.save(menuGroup);

            Menu menu = new Menu();
            menu.setId(MENU_UUID);
            menu.setName("양념치킨");
            menu.setPrice(new BigDecimal(20000));
            menu.setMenuGroup(menuGroup);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);
            menu.setMenuProducts(List.of(menuProduct));
            menuRepository.save(menu);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴 가격을 변경한다")
        @Test
        void change_menu_price() {
            // given
            Menu request = new Menu();
            request.setPrice(new BigDecimal(18000));

            // when
            Menu updatedMenu = menuService.changePrice(MENU_UUID, request);

            // then
            assertThat(updatedMenu.getPrice()).isEqualTo(request.getPrice());
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("가격은 0원 이상이어야 한다")
        @Test
        void price_must_be_positive() {
            // given
            Menu request = new Menu();
            request.setPrice(new BigDecimal(-1000));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.changePrice(MENU_UUID, request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴의 가격은 등록된 상품의 총합보다 높을 수 없다")
        @Test
        void price_must_be_lower_than_sum_of_products() {
            // given
            Menu request = new Menu();
            request.setPrice(new BigDecimal(30000));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.changePrice(MENU_UUID, request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("메뉴 전시 및 내리기")
    @Nested
    class DisplayAndHideMenuTest {

        private static final UUID MENU_UUID = UUID.randomUUID();
        private static final UUID PRODUCT_UUID = UUID.randomUUID();
        private static final UUID MENU_GROUP_UUID = UUID.randomUUID();

        @BeforeEach
        void setup() {
            Product product = new Product();
            product.setId(PRODUCT_UUID);
            product.setName("간장치킨");
            product.setPrice(new BigDecimal(19000));
            productRepository.save(product);

            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(MENU_GROUP_UUID);
            menuGroup.setName("치킨류");
            menuGroupRepository.save(menuGroup);

            Menu menu = new Menu();
            menu.setId(MENU_UUID);
            menu.setName("간장치킨");
            menu.setPrice(new BigDecimal(19000));
            menu.setMenuGroup(menuGroup);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);
            menu.setMenuProducts(List.of(menuProduct));
            menuRepository.save(menu);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴를 전시한다")
        @Test
        void display_menu() {
            // when
            Menu displayedMenu = menuService.display(MENU_UUID);

            // then
            assertThat(displayedMenu.isDisplayed()).isTrue();
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("전시될 메뉴의 가격은 등록된 상품의 총합보다 높을 수 없다")
        @Test
        void price_must_be_lower_than_sum_of_products() {
            // given
            Menu menu = menuRepository.findById(MENU_UUID)
                    .orElseThrow(NoSuchElementException::new);
            menu.setPrice(new BigDecimal(60000));
            menuRepository.save(menu);

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> menuService.display(MENU_UUID);

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴를 내린다")
        @Test
        void hide_menu() {
            // when
            Menu hiddenMenu = menuService.hide(MENU_UUID);

            // then
            assertThat(hiddenMenu.isDisplayed()).isFalse();
        }
    }

    @DisplayName("메뉴 조회하기")
    @Nested
    class FindAllMenusTest {
        private static final int TOTAL_MENU_COUNT = 6;

        @SqlGroup({
                @Sql(value = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        })
        @DisplayName("모든 메뉴를 조회한다")
        @Test
        void find_all_menus() {
            // when
            List<Menu> menus = menuService.findAll();

            // then
            assertThat(menus).hasSize(TOTAL_MENU_COUNT);
        }
    }
}
