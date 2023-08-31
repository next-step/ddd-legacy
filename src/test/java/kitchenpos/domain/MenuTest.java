package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.Fixtures.createMenuGroup;
import static kitchenpos.Fixtures.createMenuProduct;
import static kitchenpos.Fixtures.createProduct;
import static org.assertj.core.api.Assertions.assertThat;

class MenuTest {

    @Test
    @DisplayName("")
    void create() {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("메뉴");
        menu.setPrice(new BigDecimal("10000"));
        menu.setMenuGroup(createMenuGroup("메뉴그룹"));
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(createMenuProduct(1L, createProduct("반마리 치킨", new BigDecimal("1000")), 1L)));

        assertThat(menu.getId()).isNotNull();
    }

}
