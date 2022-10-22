package kitchenpos.menu.menu.domain;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Name;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("메뉴")
class MenuTest {

    @DisplayName("메뉴 그룹에 속해 있다.")
    @Test
    void requireMenuGroup() {
        assertThatThrownBy(() -> new Menu(null, createMenuProducts(new MenuProduct(new Quantity(BigDecimal.ONE)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 그룹이 없습니다.");
    }

    @DisplayName("메뉴 상품의 수량은 0개보다 작을 수 없다.")
    @Test
    void quantityCount() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        assertThatThrownBy(() -> new Menu(menuGroup, createMenuProducts(new MenuProduct(new Quantity(BigDecimal.valueOf(-1))))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수량은 0보다 작을 수 없습니다.");
    }

    private static MenuGroup createMenuGroup(UUID id, String menuGroupName) {
        return new MenuGroup(id, new Name(menuGroupName));
    }

    private static List<MenuProduct> createMenuProducts(final MenuProduct... menuProducts) {
        return Arrays.asList(menuProducts);
    }
}
