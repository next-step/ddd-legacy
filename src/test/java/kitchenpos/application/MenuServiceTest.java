package kitchenpos.application;

import kitchenpos.FixtureFactory;
import kitchenpos.IntegrationTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MenuServiceTest extends IntegrationTest {


    protected MenuGroup menuGroup;
    protected List<Product> productList;

    @BeforeEach
    void setup() {
        menuGroup = menuGroupRepository.save(FixtureFactory.createMenuGroup("추천메뉴"));
        productList = productRepository.saveAll(List.of(FixtureFactory.createProduct("양념치킨", BigDecimal.valueOf(16000)),
                FixtureFactory.createProduct("후라이드치킨", BigDecimal.valueOf(16000)),
                FixtureFactory.createProduct("간장치킨", BigDecimal.valueOf(16000))
            )
        );
    }

    private List<MenuProduct> toMenuProductList(List<Product> products) {
        return products.stream()
            .map(product -> {
                    MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setProduct(product);
                    menuProduct.setProductId(product.getId());
                    menuProduct.setQuantity(1L);
                    return menuProduct;
                }
            )
            .collect(Collectors.toList());
    }

    @Test
    @Transactional
    @DisplayName("메뉴를 모두 확인할 수 있다.")
    void find_all_menus() {
        List<Menu> menus = menuRepository.saveAll(List.of(
                FixtureFactory.createMenu("후라이드치킨 + 양념치킨", BigDecimal.valueOf(17000), true, menuGroup, toMenuProductList(productList.subList(0, 1))),
                FixtureFactory.createMenu("후라이드치킨 + 후라이드치킨", BigDecimal.valueOf(16000), true, menuGroup, toMenuProductList(productList.subList(1, 2))),
                FixtureFactory.createMenu("후라이드치킨 + 간장치킨", BigDecimal.valueOf(19000), true, menuGroup, toMenuProductList(productList.subList(2, 3)))
            )
        );

        List<Menu> allMenus = menuService.findAll();
        assertThat(allMenus).usingRecursiveComparison().isEqualTo(menus);

    }


    @Nested
    @DisplayName("메뉴를 만들 때 ")
    class CreateTest {

        @Test
        @DisplayName("성공적으로 만든다")
        void create_menu() {

            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(10000), false, menuGroup, toMenuProductList(productList));

            UUID menuId = menuService.create(menu).getId();

            Menu createdMenu = menuRepository.findById(menuId).orElseThrow(IllegalArgumentException::new);
            assertThat(createdMenu.getId()).isEqualTo(menuId);
        }

        @Test
        @DisplayName("메뉴는 반드시 제품을 하나 포함해야 한다.")
        void create_without_product() {
            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(10000), false, menuGroup, null);
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @Test
        @DisplayName("제품의 수량은 음수일 수 없다.")
        void create_negative_product() {
            List<MenuProduct> menuProducts = toMenuProductList(productList);
            menuProducts.get(0).setQuantity(-1L);
            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(10000), false, menuGroup, menuProducts);
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @Test
        @DisplayName("메뉴를 구성하는 제품들과 가게에 존재하는 제품이 일치해야 한다.(혹은 메뉴를 구성하는 제품들이 가게에 존재해야 한다.)")
        void create_not_exist_product() {
            List<MenuProduct> menuProducts = toMenuProductList(productList);
            menuProducts.get(0).setProductId(UUID.randomUUID());

            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(10000), false, menuGroup, menuProducts);

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @Test
        @DisplayName("계산된 가격(제품의 가격 * 제품의 수량)보다 비싼 메뉴를 만들 수 없다.")
        void create_expensive_menu() {

            List<MenuProduct> menuProducts = toMenuProductList(productList);

            double productPriceSum = menuProducts.stream()
                .mapToDouble(s -> s.getProduct().getPrice().doubleValue())
                .sum();

            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(productPriceSum + 1), false, menuGroup, menuProducts);
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("분류명은 공백이거나 비속어일 수 없다.")
        void create_invalid_name(String name) {
            Menu menu = FixtureFactory.createMenu(name, BigDecimal.valueOf(10000), false, menuGroup, toMenuProductList(productList));

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

    }

    @Nested
    @DisplayName("고객에게 보이는 메뉴의 유무를 정할 때")
    class HideTest {

        @Test
        @DisplayName("고객에게 가려진 메뉴를 보이게할 수 있다.")
        void show_menu() {
            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(10000), false, menuGroup, toMenuProductList(productList));
            Menu savedMenu = menuRepository.save(menu);

            menuService.display(savedMenu.getId());
            Menu shownMenu = menuRepository.findById(menu.getId()).orElseThrow(IllegalArgumentException::new);

            assertThat(shownMenu.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("계산된 가격(제품의 가격 * 제품의 수량)보다 비싼 메뉴를 보이게 할 수 없다.")
        void show_expensive_menu() {
            List<MenuProduct> menuProducts = toMenuProductList(productList);

            double productPriceSum = menuProducts.stream()
                .mapToDouble(s -> s.getProduct().getPrice().doubleValue())
                .sum();

            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(productPriceSum + 100), false, menuGroup, menuProducts);
            Menu savedMenu = menuRepository.save(menu);

            assertThrows(IllegalStateException.class, () -> menuService.display(savedMenu.getId()));
        }

        @Test
        @DisplayName("고객에게 보이는 메뉴를 가릴 수 있다.")
        void hide_menu() {
            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(10000), true, menuGroup, toMenuProductList(productList));
            Menu savedMenu = menuRepository.save(menu);

            menuService.hide(savedMenu.getId());
            Menu hiddenMenu = menuRepository.findById(menu.getId()).orElseThrow(IllegalArgumentException::new);

            assertThat(hiddenMenu.isDisplayed()).isFalse();
        }
    }


    @Nested
    @DisplayName("가격을 변경할 때")
    class ChangePriceTest {

        @Test
        @DisplayName("가격을 성공적으로 변경할 수 있다.")
        void change_price() {
            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(10000), true, menuGroup, toMenuProductList(productList));
            Menu savedMenu = menuRepository.save(menu);

            BigDecimal changedPrice = BigDecimal.valueOf(5000);
            savedMenu.setPrice(changedPrice);

            menuService.changePrice(savedMenu.getId(), savedMenu);
            Menu changedMenu = menuRepository.findById(savedMenu.getId()).orElseThrow(IllegalArgumentException::new);

            assertThat(changedMenu.getPrice().compareTo(changedPrice)).isZero();
        }

        @Test
        @DisplayName("가격은 음수일 수 없다.")
        void change_negative_price() {
            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(10000), true, menuGroup, toMenuProductList(productList));
            Menu savedMenu = menuRepository.save(menu);

            BigDecimal changedPrice = BigDecimal.valueOf(-1);
            savedMenu.setPrice(changedPrice);

            assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(savedMenu.getId(), savedMenu));
        }

        @Test
        @DisplayName("메뉴의 가격이 계산된 가격(제품의 가격 * 제품의 수량)보다 비싸게 가격을 변경할 수 없다.")
        void change_expensive_price() {
            List<MenuProduct> menuProducts = toMenuProductList(productList);

            double productPriceSum = menuProducts.stream()
                .mapToDouble(s -> s.getProduct().getPrice().doubleValue())
                .sum();

            Menu menu = FixtureFactory.createMenu("메뉴", BigDecimal.valueOf(productPriceSum), true, menuGroup, menuProducts);
            Menu savedMenu = menuRepository.save(menu);

            BigDecimal changedPrice = BigDecimal.valueOf(productPriceSum + 100);
            savedMenu.setPrice(changedPrice);

            assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(savedMenu.getId(), savedMenu));
        }

    }


}
