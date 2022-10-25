package kitchenpos.menu.menu.domain;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("메뉴")
class MenuTest {

    @DisplayName("메뉴 그룹에 속해 있다.")
    @Test
    void requireMenuGroup() {
        assertThatThrownBy(() -> createMenu(null, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.ONE)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 그룹이 없습니다.");
    }

    @DisplayName("메뉴 상품의 수량은 0개보다 작을 수 없다.")
    @Test
    void quantityCount() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        assertThatThrownBy(() -> createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(-1))), new Price(BigDecimal.ONE)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수량은 0보다 커야합니다.");
    }

    @DisplayName("메뉴 가격이 0원보다 작을 수 없다.")
    @Test
    void menuPriceOverZero() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        assertThatThrownBy(() -> createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.valueOf(-1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("메뉴 상품 목록은 비어 있을 수 없다.")
    @Test
    void menuProductsNotEmpty() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        assertThatThrownBy(() -> createMenu(menuGroup, "메뉴명", false, null, new Price(BigDecimal.ONE)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 상품 목록은 비어 있을 수 없습니다.");
    }

    @DisplayName("메뉴 생성 시 메뉴 가격을 필수로 입력받는다.")
    @Test
    void requireMenuPrice() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        assertThatThrownBy(() -> createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 가격을 입력해주세요.");
    }

    @DisplayName("메뉴 가격은 필수로 입력받는다.")
    @Test
    void changeMenuPrice_fail() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.TEN));
        assertThatThrownBy(() -> menu.changePrice(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격을 입력해주세요");
    }

    @DisplayName("메뉴 가격을 변경할 수 있다.")
    @Test
    void changeMenuPrice() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.TEN));
        assertThat(menu.getPrice()).isEqualTo(BigDecimal.TEN);
        menu.changePrice(new Price(BigDecimal.valueOf(20)));
        assertThat(menu.getPrice()).isEqualTo(BigDecimal.valueOf(20));

    }

    @DisplayName("메뉴 가격은 0원보다 크다.")
    @Test
    void changeMinimumPrice() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.TEN));
        assertThatThrownBy(() -> menu.changePrice(new Price(BigDecimal.valueOf(-1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void hideMenu() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.TEN));
        menu.display();
        assertThat(menu.isDisplayed()).isTrue();
        menu.hide();
        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 보여줄 수 있다.")
    @Test
    void displayMenu() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.TEN));
        menu.hide();
        assertThat(menu.isDisplayed()).isFalse();
        menu.display();
        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴의 가격이 메뉴 상품의 합보다 크면 메뉴를 숨긴다.")
    @Test
    void displayMenuByPrice() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.ONE));
        menu.display();
        assertThat(menu.isDisplayed()).isTrue();
        assertThat(menu.sumMenuProducts()).isEqualTo(BigDecimal.TEN);
        menu.changePrice(new Price(BigDecimal.valueOf(11)));
        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴의 가격이 메뉴 상품의 합보다 클 수 없다.")
    @Test
    void menuPriceOverSumMenuPrice() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.valueOf(11)));
        assertThatThrownBy(() -> menu.display())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴의 가격이 메뉴 상품의 합보다 클 수 없다.");
    }

    @DisplayName("상품 가격의 총합은 0원보다 크다.")
    @Test
    void sumMenuProductsOverZero() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        assertThatThrownBy(() -> createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.ZERO)), new Quantity(1))), new Price(BigDecimal.valueOf(11))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 가격의 총합은 0원보다 크다.");
    }

    //    메뉴 생성 시 메뉴명 / 메뉴 가격 / 메뉴 그룹 아이디 / 메뉴 전시 여부 / 메뉴 상품 목록을 입력 받는다.
    @DisplayName("주문 생성 시 주문 타입 / 주문 테이블 아이디 / 주문 항목 목록을 입력 받는다.")
    @Test
    void createMenu() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        assertThatNoException().isThrownBy(() -> createMenu(menuGroup, "메뉴명", false, createMenuProducts(new MenuProduct(new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.ONE)), new Quantity(1))), new Price(BigDecimal.valueOf(11))));
    }

    private static Menu createMenu(MenuGroup menuGroup, String name, boolean isProfanity, List<MenuProduct> menuProducts, Price price) {
        return new Menu(UUID.randomUUID(), new Name(name, isProfanity), menuGroup, menuProducts, price);
    }

    private static MenuGroup createMenuGroup(UUID id, String menuGroupName) {
        return new MenuGroup(id, new Name(menuGroupName, false));
    }

    private static List<MenuProduct> createMenuProducts(final MenuProduct... menuProducts) {
        return Arrays.asList(menuProducts);
    }
}
