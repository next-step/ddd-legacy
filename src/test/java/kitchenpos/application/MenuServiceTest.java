package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql({"/truncate-all.sql", "/insert-menu-integration.sql"})
@SpringBootTest
class MenuServiceTest {
    @Autowired
    private MenuService sut;

    @DisplayName("매뉴를 생성할 수 있다.")
    @Test
    void create() {
        final long quantity = 1L;
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final String name = "치킨세트";
        final BigDecimal price = new BigDecimal("20");
        final boolean displayed = true;
        final UUID menuGroupId = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");

        final Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        final Menu response = sut.create(request);

        assertThat(response.getName()).isEqualTo(name);
    }

    @DisplayName("매뉴의 가격은 비어있을 수 없다.")
    @NullSource
    @ParameterizedTest
    void createWithEmptyPrice(final BigDecimal price) {
        final long quantity = 1L;
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final String name = "치킨세트";
        final boolean displayed = true;
        final UUID menuGroupId = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");

        final Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매뉴의 가격은 0보다 크거나 같다.")
    @Test
    void createWithLessThanZeroPrice() {
        final long quantity = 1L;
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final String name = "치킨세트";
        final BigDecimal price = new BigDecimal("-1");
        final boolean displayed = true;
        final UUID menuGroupId = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");

        final Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 메뉴그룹을 필수값으로 갖는다.")
    @Test
    void createWithNoneMenugroup() {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final String name = "치킨 세트";
        final BigDecimal price = new BigDecimal("20.00");

        final boolean displayed = true;
        final long quantity = 3L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580");

        final Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴는 메뉴상품을 필수값으로 갖는다.")
    @Test
    void createWithNoneMenuProduct() {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b11");
        final String name = "치킨 세트";
        final BigDecimal price = new BigDecimal("20");

        final boolean displayed = true;
        final long quantity = 3L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");

        final Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품의 갯수는 0보다 크거나 같은 값이다.")
    @Test
    void createWithLessThanZero() {
        final long quantity = -1L;
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final String name = "치킨세트";
        final BigDecimal price = new BigDecimal("20");
        final boolean displayed = true;
        final UUID menuGroupId = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");

        final Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격은 상품의 충 가격 보다 작아야 한다.")
    @Test
    void createWithBigPrice() {
        final long quantity = 1L;
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final String name = "치킨 세트";
        final BigDecimal price = new BigDecimal("50000");
        final boolean displayed = true;
        final UUID menuGroupId = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");

        final Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    
    @DisplayName("메뉴 이름은 비어있을 수 없다.")
    @NullSource
    @ParameterizedTest
    void createWithEmptyName(final String name) {
        final long quantity = 1L;
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final BigDecimal price = new BigDecimal("20");
        final boolean displayed = true;
        final UUID menuGroupId = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");

        final Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    
    @DisplayName("메뉴 이름은 비속어를 포함할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = "fuck, goddamn")
    void createWithPurgomalum(final String name) {
        final long quantity = 1L;
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final BigDecimal price = new BigDecimal("20");
        final boolean displayed = true;
        final UUID menuGroupId = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");

        final Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        final UUID menuId = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
        final Menu request = new Menu(new BigDecimal("14000"));

        final Menu response = sut.changePrice(menuId, request);

        assertThat(response.getPrice()).isEqualTo(new BigDecimal("14000"));
    }

    @DisplayName("메뉴의 가격이 메뉴 상품 가격의 합보다 크면 숨김 상품이 된다.")
    @Test
    void changePriceWithBigPrice() {
        final UUID menuId = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
        final Menu request = new Menu(new BigDecimal("20000"));

        assertThatThrownBy(() -> sut.changePrice(menuId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 비공개에서 공개로 설정할 수 있다.")
    @Test
    void display() {
        final UUID hideMenuId = UUID.fromString("e1254913-8608-46aa-b23a-a07c1dcbc648");

        final Menu response = sut.display(hideMenuId);

        assertThat(response.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 비공개에서 공개로 설정할 수 있다.")
    @Test
    void hide() {
        final UUID menuId = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");

        final Menu response = sut.hide(menuId);

        assertThat(response.isDisplayed()).isFalse();
    }

    @DisplayName("상품을 여러개 조회할 수 있다.")
    @Test
    void findAll() {
        final List<Menu> response = sut.findAll();

        assertThat(response).hasSize(2);
    }
}
