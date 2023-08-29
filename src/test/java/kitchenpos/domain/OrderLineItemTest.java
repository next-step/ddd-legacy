package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static org.assertj.core.api.Assertions.assertThat;

class OrderLineItemTest {
    @DisplayName("주문 목록 생성")
    @Test
    void test1() {
        final Long seq = 1L;
        final Menu menu = createMenu();
        final long quantity = 3;
        final UUID menuId = UUID.randomUUID();
        final BigDecimal price = BigDecimal.ONE;

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(seq);
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);

        assertThat(orderLineItem.getSeq()).isEqualTo(seq);
        assertThat(orderLineItem.getMenu()).isEqualTo(menu);
        assertThat(orderLineItem.getQuantity()).isEqualTo(quantity);
        assertThat(orderLineItem.getMenuId()).isEqualTo(menuId);
        assertThat(orderLineItem.getPrice()).isEqualTo(price);
    }
}