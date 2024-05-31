package kitchenpos.fixtures;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Fixture {

    public static Product fixtureProduct() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(28_000L));
        product.setName("치킨");
        return product;
    }

    public static MenuGroup fixtureMenuGroup() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("치킨세트");
        return menuGroup;
    }

    public static MenuProduct fixtureMenuProduct() {
        final MenuProduct menuProduct = new MenuProduct();
        final Product product = Fixture.fixtureProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(1L);
        return menuProduct;
    }

    public static List<MenuProduct> fixtureMenuProducts() {
        final MenuProduct menuProduct = Fixture.fixtureMenuProduct();
        return List.of(menuProduct);
    }

    public static Menu fixtureMenu() {
        final Menu menu = new Menu();
        menu.setName("치킨");
        menu.setPrice(BigDecimal.valueOf(28_000L));
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuGroup(Fixture.fixtureMenuGroup());
        menu.setMenuProducts(Fixture.fixtureMenuProducts());
        menu.setDisplayed(true);
        return menu;
    }

    public static OrderTable fixtureOrderTable() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setOccupied(false);
        orderTable.setNumberOfGuests(0);
        return orderTable;
    }

    public static OrderLineItem fixtureOrderLineItem() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        final Menu menu = Fixture.fixtureMenu();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getMenuGroupId());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(28_000L));
        return orderLineItem;
    }

    public static List<OrderLineItem> fixtureOrderLineItems() {
        final OrderLineItem orderLineItem = Fixture.fixtureOrderLineItem();
        return List.of(orderLineItem);
    }

    public static Order fixtureOrder() {
        final Order order = new Order();
        final OrderTable orderTable = Fixture.fixtureOrderTable();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(Fixture.fixtureOrderLineItems());
        order.setDeliveryAddress("주소");
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }
}
