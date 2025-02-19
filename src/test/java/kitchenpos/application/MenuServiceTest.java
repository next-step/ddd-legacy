package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
class MenuServiceTest {
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurgomalumClient purgomalumClient;

    private MenuGroup menuGroup;
    private Product product;

    @BeforeEach
    void setUp() {
        menuRepository.deleteAll();
        menuGroupRepository.deleteAll();
        productRepository.deleteAll();

        menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리메뉴");
        menuGroupRepository.save(menuGroup);

        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(16000));
        productRepository.save(product);
    }

    private MenuProduct createMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private Menu createMenu(String name, BigDecimal price, List<MenuProduct> menuProducts, boolean displayed) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(displayed);
        return menuRepository.save(menu);
    }

    @Nested
    @DisplayName("메뉴 생성")
    class CreateMenuTest {

        @Test
        @DisplayName("메뉴를 생성할 수 있다.")
        void create() {
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu request = new Menu();
            request.setId(UUID.randomUUID());
            request.setDisplayed(true);
            request.setName("후라이드");
            request.setPrice(BigDecimal.valueOf(16000));
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of(menuProduct));

            Menu result = menuService.create(request);

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getId()).isNotNull();
            Assertions.assertThat(result.getName()).isEqualTo("후라이드");
            Assertions.assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(16000));
            Assertions.assertThat(result.getMenuProducts()).hasSize(1);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("메뉴 가격이 null 또는 0원 미만이면 IllegalArgumentException 예외 발생")
        void cannotCreateMenuWithNullPrice(BigDecimal invalidPrice) {
            Menu request = new Menu();
            request.setId(UUID.randomUUID());
            request.setPrice(invalidPrice);

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @ParameterizedTest
        @ValueSource(longs = -1)
        @DisplayName("메뉴 가격이 0원 미만이면 IllegalArgumentException 예외 발생")
        void cannotCreateMenuWithInvalidPrice(Long invalidPrice) {
            Menu request = new Menu();
            request.setId(UUID.randomUUID());
            request.setPrice(BigDecimal.valueOf(invalidPrice));

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @Test
        @DisplayName("메뉴 그룹에 속하지 않으면 NoSuchElementException 예외 발생")
        void cannotCreateMenuWithoutMenuGroup() {
            Menu request = new Menu();
            request.setId(UUID.randomUUID());
            request.setPrice(BigDecimal.valueOf(10000));
            request.setMenuGroupId(UUID.randomUUID());

            Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @Test
        @DisplayName("메뉴 상품이 없으면 IllegalArgumentException 예외 발생")
        void cannotCreateMenuWithoutMenuProduct() {
            Menu request = new Menu();
            request.setId(UUID.randomUUID());
            request.setName("세마리세트");
            request.setPrice(BigDecimal.valueOf(25000));
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(null);

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }
    }

    @Nested
    @DisplayName("메뉴 가격 변경")
    class ChangeMenuPriceTest {

        @Test
        @DisplayName("메뉴 가격을 변경할 수 있다.")
        void changePrice() {
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu("후라이드치킨", BigDecimal.valueOf(16000), List.of(menuProduct), true);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(15000));

            Menu result = menuService.changePrice(menu.getId(), request);

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(15000));
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("변경할 가격이 null 또는 0원 미만이면 IllegalArgumentException 예외 발생")
        void cannotChangePriceWithNullPrice(BigDecimal invalidPrice) {
            Menu request = new Menu();
            request.setId(UUID.randomUUID());
            request.setPrice(invalidPrice);
            request.setMenuGroupId(menuGroup.getId());

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @ParameterizedTest
        @ValueSource(longs = -1)
        @DisplayName("변경할 가격이  0원 미만이면 IllegalArgumentException 예외 발생")
        void cannotChangePriceWithInvalidPrice(Long invalidPrice) {
            Menu request = new Menu();
            request.setId(UUID.randomUUID());
            request.setPrice(BigDecimal.valueOf(invalidPrice));
            request.setMenuGroupId(menuGroup.getId());

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }
    }

    @Nested
    @DisplayName("메뉴 상태 변경")
    class ChangeMenuStatusTest {

        @Test
        @DisplayName("메뉴를 판매 가능 상태로 변경할 수 있다.")
        void display() {
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu("후라이드치킨", BigDecimal.valueOf(16000), List.of(menuProduct), false);

            Menu result = menuService.display(menu.getId());

            Assertions.assertThat(result.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("메뉴를 판매 불가능 상태로 변경할 수 있다.")
        void hide() {
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu("후라이드치킨", BigDecimal.valueOf(16000), List.of(menuProduct), true);

            Menu result = menuService.hide(menu.getId());

            Assertions.assertThat(result.isDisplayed()).isFalse();
        }
    }

    @Nested
    @DisplayName("메뉴 조회")
    class FindMenuTest {

        @Test
        @DisplayName("모든 메뉴를 조회할 수 있다.")
        void findAll() {
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu("후라이드치킨", BigDecimal.valueOf(16000), List.of(menuProduct), false);

            List<Menu> result = menuService.findAll();

            Assertions.assertThat(result).hasSize(1);
            Assertions.assertThat(result)
                    .extracting(Menu::getId)
                    .containsExactly(menu.getId());
        }
    }
}
