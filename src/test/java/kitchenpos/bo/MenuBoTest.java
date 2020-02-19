package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import kitchenpos.menu.MenuFixtures;
import kitchenpos.menu.supports.MenuBoFactory;
import kitchenpos.menu.supports.MenuDaoWithConstraint;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuBuilder;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;

class MenuBoTest {

    private static final String VALID_MENU_NAME = "메뉴";
    private static final long DEFAULT_QUANTITY = 2;

    private final static List<Product> products;
    private final static MenuGroup menuGroup;

    static {
        products = Collections.unmodifiableList(MenuFixtures.productFixture());
        menuGroup = MenuFixtures.menuGroupFixture();
    }

    @Test
    @DisplayName("메뉴를 생성한다. 상품의 종류는 중복 가능하다.")
    void create() {
        MenuBo sut = MenuBoFactory.withFixturesAndExternalConstraint(products, menuGroup);

        Menu menu = menuBuilderWithValidCondition().build();

        assertThat(sut.create(menu))
            .isEqualTo(menu);
    }

    private static MenuBuilder menuBuilderWithValidCondition() {
        return MenuBuilder.aMenu()
                          .withName(VALID_MENU_NAME)
                          .withPrice(calculateMaxMenuPrice(menuProductsWithValidCondition(DEFAULT_QUANTITY)))
                          .withMenuGroupId(menuGroup.getId())
                          .withMenuProducts(menuProductsWithValidCondition(DEFAULT_QUANTITY));
    }

    private static List<MenuProduct> menuProductsWithValidCondition(long eachQuantity) {
        List<MenuProduct> menuProducts = new ArrayList<>();
        Product product = products.get(0);
        menuProducts.add(menuProductFrom(product, eachQuantity));
        menuProducts.add(menuProductFrom(product, eachQuantity));
        return menuProducts;
    }

    //    > 상품 가격의 총합 : 상품 가격 * 상품 갯수
    private static BigDecimal calculateMaxMenuPrice(List<MenuProduct> menuProducts) {
        return MenuFixtures.PRICE_OF_EACH_PRODUCT
            .multiply(BigDecimal.valueOf(menuProducts.stream()
                                                     .mapToLong(MenuProduct::getQuantity)
                                                     .sum()));
    }

    @MethodSource("create_invalid_invariants_cases")
    @ParameterizedTest
    @DisplayName("메뉴를 생성한다. 불변식 위반")
    void create_when_invariant_is_invalid(String invariantDescription,
                                          MenuBo sut,
                                          Menu menu,
                                          Class<Throwable> expected) {
        assertThatThrownBy(() -> sut.create(menu))
            .as(invariantDescription)
            .isExactlyInstanceOf(expected);
    }

    private static Stream<Arguments> create_invalid_invariants_cases() {
        return Stream.of(Arguments.of("메뉴 생성 시 메뉴의 가격은 필수이다.",
                                      MenuBoFactory.withFixtures(products, menuGroup),
                                      menuBuilderWithValidCondition().withPrice(null)
                                                                     .build(),
                                      IllegalArgumentException.class),

                         Arguments.of("메뉴의 가격은 0원 이상이다.",
                                      MenuBoFactory.withFixtures(products, menuGroup),
                                      menuBuilderWithValidCondition().withPrice(BigDecimal.ONE.negate())
                                                                     .build(),
                                      IllegalArgumentException.class),

                         Arguments.of("메뉴의 가격은 해당 메뉴의 속한 모든 종류별 상품 가격의 총합보다 클 수 없다.",
                                      MenuBoFactory.withFixtures(products, menuGroup),
                                      menuBuilderWithValidCondition().withMenuProducts(menuProductsWithValidCondition(DEFAULT_QUANTITY - 1))
                                                                     .build(),
                                      IllegalArgumentException.class),

                         Arguments.of("메뉴 생성 시 메뉴는 1개의 메뉴 그룹에 소속되어야 한다.",
                                      MenuBoFactory.withFixtures(products, menuGroup),
                                      menuBuilderWithValidCondition().withMenuGroupId(null)
                                                                     .build(),
                                      IllegalArgumentException.class),

                         Arguments.of("메뉴가 소속될 메뉴 그룹은 기존에 생성되어 있어야 한다.",
                                      MenuBoFactory.withFixtures(products),
                                      menuBuilderWithValidCondition().build(),
                                      IllegalArgumentException.class),

                         Arguments.of("메뉴 생성 시 메뉴는 상품을 1 종류 이상 갖는다.",
                                      MenuBoFactory.withFixtures(products, menuGroup),
                                      menuBuilderWithValidCondition().withMenuProducts(Collections.emptyList())
                                                                     .build(),
                                      IllegalArgumentException.class),

                         Arguments.of("메뉴가 가질 수 있는 상품은 기존에 생성되어 있어야 한다.",
                                      MenuBoFactory.withFixtures(Collections.emptyList(), menuGroup),
                                      menuBuilderWithValidCondition().build(),
                                      IllegalArgumentException.class),

                         Arguments.of("포함 할 상품의 종류 별로 갯수를 1개 이상 갖는다.",
                                      MenuBoFactory.withFixtures(products, menuGroup),
                                      menuBuilderWithValidCondition().withMenuProducts(menuProductsWithValidCondition(0))
                                                                     .build(),
                                      IllegalArgumentException.class),

                         Arguments.of("메뉴의 이름은 필수이다.",
                                      MenuBoFactory.withFixturesAndExternalConstraint(products, menuGroup),
                                      menuBuilderWithValidCondition().withName(null)
                                                                     .build(),
                                      MenuDaoWithConstraint.MENU_CONSTRAINT_EXCEPTION.getClass()));
    }

    private static MenuProduct menuProductFrom(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
