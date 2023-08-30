package kitchenpos.domain;

import kitchenpos.fixture.MenuProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProductWithDefaultId;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;

class MenuTest {
    @DisplayName("메뉴 생성")
    @Test
    void test1() {
        final UUID id = UUID.randomUUID();
        final String name = "치킨";
        final BigDecimal price = BigDecimal.TEN;
        final MenuGroup menuGroup = createMenuGroup(UUID.randomUUID());
        final boolean displayed = true;
        final Product product = createProduct();
        final List<MenuProduct> menuProducts = List.of(MenuProductFixture.createMenuProduct(1L, product, 10));
        final UUID menuGroupId = menuGroup.getId();

        final Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menuGroupId);

        assertThat(menu.getId()).isEqualTo(id);
        assertThat(menu.getName()).isEqualTo(name);
        assertThat(menu.getPrice()).isEqualTo(price);
        assertThat(menu.getMenuGroup()).isEqualTo(menuGroup);
        assertThat(menu.isDisplayed()).isEqualTo(displayed);
        assertThat(menu.getMenuProducts()).isEqualTo(menuProducts);
        assertThat(menu.getMenuGroupId()).isEqualTo(menuGroupId);
    }
}