package kitchenpos.constant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

public final class Fixtures {
    public static final Product PRODUCT = new Product();
    public static final MenuGroup MENU_GROUP = new MenuGroup();
    public static final MenuProduct MENU_PRODUCT = new MenuProduct();
    public static final Menu MENU = new Menu();
    public static final OrderTable ORDER_TABLE = new OrderTable();
    public static final OrderLineItem ORDER_LINE_ITEM = new OrderLineItem();
    public static final Order ORDER = new Order();

    static {
        initialize();
    }

    public static void initialize() {
        PRODUCT.setId(UUID.randomUUID());
        PRODUCT.setName("SampleProduct");
        PRODUCT.setPrice(BigDecimal.valueOf(5000));

        MENU_GROUP.setId(UUID.randomUUID());
        MENU_GROUP.setName("SampleMenuGroup");

        MENU_PRODUCT.setSeq(1L);
        MENU_PRODUCT.setProduct(PRODUCT);
        MENU_PRODUCT.setQuantity(1);

        MENU.setId(UUID.randomUUID());
        MENU.setName("SampleMenu");
        MENU.setPrice(BigDecimal.valueOf(5000));
        MENU.setMenuGroup(MENU_GROUP);
        MENU.setDisplayed(true);
        MENU.setMenuProducts(List.of(MENU_PRODUCT));
        MENU.setMenuGroupId(MENU_GROUP.getId());

        ORDER_TABLE.setId(UUID.randomUUID());
        ORDER_TABLE.setName("SampleOrderTable");
        ORDER_TABLE.setNumberOfGuests(5);
        ORDER_TABLE.setOccupied(true);

        ORDER_LINE_ITEM.setSeq(1L);
        ORDER_LINE_ITEM.setMenu(MENU);
        ORDER_LINE_ITEM.setQuantity(1L);
        ORDER_LINE_ITEM.setPrice(BigDecimal.valueOf(5000));

        ORDER.setId(UUID.randomUUID());
        ORDER.setType(OrderType.EAT_IN);
        ORDER.setStatus(OrderStatus.WAITING);
        ORDER.setOrderDateTime(LocalDateTime.parse("2022-01-01T09:00:00"));
        ORDER.setDeliveryAddress("SampleAddress");
        ORDER.setOrderTable(ORDER_TABLE);
        ORDER.setOrderLineItems(List.of(ORDER_LINE_ITEM));
    }

    public static OrderTable createSampleOrderTable(int numberOfGuests, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("SampleOrderTable");
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static Order createSampleOrder(OrderType orderType, OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.parse("2022-01-01T09:00:00"));
        order.setOrderLineItems(List.of(Fixtures.ORDER_LINE_ITEM));

        if (orderType == OrderType.EAT_IN) {
            order.setOrderTable(ORDER_TABLE);
        }
        if (orderType == OrderType.DELIVERY) {
            order.setDeliveryAddress("SampleAddress");
        }
        return order;
    }
}
