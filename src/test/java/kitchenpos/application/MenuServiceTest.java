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

@SpringBootTest
class MenuServiceTest {
    @Autowired
    private MenuService sut;

    @Sql({"/insert-menu-group.sql", "/insert-product.sql"})
    @DisplayName("매뉴를 생성할 수 있다.")
    @Test
    void create() {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final String name = "치킨 세트";
        final BigDecimal price = new BigDecimal("20.00");

        final boolean displayed = true;
        final long quantity = 3L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580");

        Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        final Menu response = sut.create(request);

        assertThat(response.getName()).isEqualTo(name);
    }

    @Sql({"/insert-menu-group.sql", "/insert-product.sql"})
    @DisplayName("매뉴의 가격은 비어있을 수 없다.")
    @NullSource
    @ParameterizedTest
    void createWithEmptyPrice(final BigDecimal price) {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final String name = "치킨 세트";

        final boolean displayed = true;
        final long quantity = 3L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580");

        Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Sql({"/insert-menu-group.sql", "/insert-product.sql"})
    @DisplayName("매뉴의 가격은 0보다 크거나 같다.")
    @Test
    void createWithLessThanZeroPrice() {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final String name = "치킨 세트";
        final BigDecimal price = new BigDecimal("-1");

        final boolean displayed = true;
        final long quantity = 3L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580");

        Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Sql("/insert-product.sql")
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

        Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Sql("/insert-menu-group.sql")
    @DisplayName("메뉴는 메뉴상품을 필수값으로 갖는다.")
    @Test
    void createWithNoneMenuProduct() {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final String name = "치킨 세트";
        final BigDecimal price = new BigDecimal("20.00");

        final boolean displayed = true;
        final long quantity = 3L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580");

        Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Sql({"/insert-menu-group.sql", "/insert-product.sql"})
    @DisplayName("메뉴 상품의 갯수는 0보다 크거나 같은 값이다.")
    @Test
    void createWithLessThanZero() {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final String name = "치킨 세트";
        final BigDecimal price = new BigDecimal("20.00");

        final boolean displayed = true;
        final long quantity = -1L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580");

        Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Sql({"/insert-menu-group.sql", "/insert-product.sql"})
    @DisplayName("메뉴 가격은 상품의 충 가격 보다 작아야 한다.")
    @Test
    void createWithBigPrice() {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final String name = "치킨 세트";
        final BigDecimal price = new BigDecimal("20000");

        final boolean displayed = true;
        final long quantity = -1L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580");

        Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Sql({"/insert-menu-group.sql", "/insert-product.sql"})
    @DisplayName("메뉴 이름은 비어있을 수 없다.")
    @NullSource
    @ParameterizedTest
    void createWithEmptyName(final String name) {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final BigDecimal price = new BigDecimal("20.00");

        final boolean displayed = true;
        final long quantity = -1L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580");

        Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Sql({"/insert-menu-group.sql", "/insert-product.sql"})
    @DisplayName("메뉴 이름은 비속어를 포함할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"fuck, goddamn"})
    void createWithPurgomalum(final String name) {
        final UUID productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        final BigDecimal price = new BigDecimal("20.00");

        final boolean displayed = true;
        final long quantity = -1L;
        final List<MenuProduct> menuProducts = List.of(new MenuProduct(quantity, productId));
        final UUID menuGroupId = UUID.fromString("f1860abc-2ea1-411b-bd4a-baa44f0d5580");

        Menu request = new Menu(name, price, displayed, menuProducts, menuGroupId);

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
