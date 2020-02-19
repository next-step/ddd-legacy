package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kitchenpos.model.Menu;
import kitchenpos.model.MenuBuilder;
import kitchenpos.model.OrderBuilder;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderLineItemBuilder;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import kitchenpos.model.OrderTableBuilder;
import kitchenpos.order.supports.OrderBoFactory;

class OrderBoTest {

    @Test
    @DisplayName("주문을 생성한다. 주문은 최초 생성 시 조리중 상태를 갖는다.")
    void create() {
        OrderTable orderedTable = OrderTableBuilder.anOrderTable()
                                                   .build();
        List<Menu> orderedMenus = Arrays.asList(MenuBuilder.aMenu()
                                                           .build(),
                                                MenuBuilder.aMenu()
                                                           .build());

        OrderBo sut = OrderBoFactory.withFixture(orderedTable,
                                                 orderedMenus);

        List<OrderLineItem> orderedItems = orderedMenus.stream()
                                                       .map(m -> OrderLineItemBuilder.anOrderLineItem()
                                                                                     .withMenuId(m.getId())
                                                                                     .build())
                                                       .collect(Collectors.toList());

        String actual = sut.create(OrderBuilder.anOrder()
                                               .withOrderTableId(OrderBoFactory.EXIST_TABLE_ID)
                                               .withOrderLineItems(orderedItems)
                                               .build()).getOrderStatus();

        assertEquals(OrderStatus.COOKING.name(), actual);
    }

    @Test
    @DisplayName("주문을 생성한다. 생성 될 주문은 1개 이상의 주문 항목을 가져야 한다.")
    void create_when_number_of_orderLineItem_is_zero() {
        OrderTable orderedTable = OrderTableBuilder.anOrderTable()
                                                   .build();
        List<Menu> orderedMenus = Arrays.asList(MenuBuilder.aMenu()
                                                           .build(),
                                                MenuBuilder.aMenu()
                                                           .build());

        OrderBo sut = OrderBoFactory.withFixture(orderedTable,
                                                 orderedMenus);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.create(OrderBuilder.anOrder()
                                                     .withOrderTableId(OrderBoFactory.EXIST_TABLE_ID)
                                                     .withOrderLineItems(Collections.emptyList())
                                                     .build()));
    }

    @Test
    @DisplayName("주문을 생성한다. 생성 될 주문의 주문 항목들의 메뉴는 중복될 수 없다.")
    void create_when_menus_are_duplicated() {
        OrderTable orderedTable = OrderTableBuilder.anOrderTable()
                                                   .build();
        Menu dulplicatedOrderedMenu = MenuBuilder.aMenu()
                                                 .build();

        OrderBo sut = OrderBoFactory.withFixture(orderedTable,
                                                 Collections.singletonList(dulplicatedOrderedMenu));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.create(OrderBuilder.anOrder()
                                                     .withOrderTableId(OrderBoFactory.EXIST_TABLE_ID)
                                                     .withOrderLineItems(Arrays.asList(OrderLineItemBuilder.anOrderLineItem()
                                                                                                           .withMenuId(dulplicatedOrderedMenu.getId())
                                                                                                           .build(),
                                                                                       OrderLineItemBuilder.anOrderLineItem()
                                                                                                           .withMenuId(dulplicatedOrderedMenu.getId())
                                                                                                           .build()))
                                                     .build()));
    }

    @Test
    @DisplayName("주문을 생성한다. 생성 될 주문은 기존에 생성된 테이블에 속해 있어야 한다.")
    void create_when_table_is_not_exists() {
        OrderTable orderedTable = OrderTableBuilder.anOrderTable()
                                                   .build();
        List<Menu> orderedMenus = Arrays.asList(MenuBuilder.aMenu()
                                                           .build(),
                                                MenuBuilder.aMenu()
                                                           .build());

        OrderBo sut = OrderBoFactory.withFixture(orderedTable,
                                                 orderedMenus);

        List<OrderLineItem> orderedItems = orderedMenus.stream()
                                                       .map(m -> OrderLineItemBuilder.anOrderLineItem()
                                                                                     .withMenuId(m.getId())
                                                                                     .build())
                                                       .collect(Collectors.toList());

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.create(OrderBuilder.anOrder()
                                                     .withOrderLineItems(orderedItems)
                                                     .build()));
    }

    @Test
    @DisplayName("주문을 생성한다. 생성 될 주문이 속할 테이블은 비어 있지 않아야 한다.")
    void create_when_table_is_empty() {
        OrderTable orderedTable = OrderTableBuilder.anOrderTable()
                                                   .withEmpty(true)
                                                   .build();
        List<Menu> orderedMenus = Arrays.asList(MenuBuilder.aMenu()
                                                           .build(),
                                                MenuBuilder.aMenu()
                                                           .build());

        OrderBo sut = OrderBoFactory.withFixture(orderedTable,
                                                 orderedMenus);

        List<OrderLineItem> orderedItems = orderedMenus.stream()
                                                       .map(m -> OrderLineItemBuilder.anOrderLineItem()
                                                                                     .withMenuId(m.getId())
                                                                                     .build())
                                                       .collect(Collectors.toList());

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.create(OrderBuilder.anOrder()
                                                     .withOrderTableId(OrderBoFactory.EXIST_TABLE_ID)
                                                     .withOrderLineItems(orderedItems)
                                                     .build()));
    }

    @Test
    @DisplayName("주문의 상태를 변경한다.")
    void changeOrderStatus() {
        OrderBo sut = OrderBoFactory.withFixture(OrderBuilder.anOrder()
                                                             .withOrderStatus(OrderStatus.MEAL.name())
                                                             .build());

        String actualOrderStatus = sut.changeOrderStatus(OrderBoFactory.EXIST_ORDER_ID,
                                                         OrderBuilder.anOrder()
                                                                     .withOrderStatus(OrderStatus.COMPLETION.name())
                                                                     .build()).getOrderStatus();

        assertEquals("COMPLETION", actualOrderStatus);
    }

    @Test
    @DisplayName("주문의 상태를 변경한다. ")
    void changeOrderStatus_when_order_status_is_COMPLETION() {
        OrderBo sut = OrderBoFactory.withFixture(OrderBuilder.anOrder()
                                                             .withOrderStatus(OrderStatus.COMPLETION.name())
                                                             .build());

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> sut.changeOrderStatus(OrderBoFactory.EXIST_ORDER_ID,
                                                    OrderBuilder.anOrder()
                                                                .withOrderStatus(OrderStatus.MEAL.name())
                                                                .build()));
    }
}