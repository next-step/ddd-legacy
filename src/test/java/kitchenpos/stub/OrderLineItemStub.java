package kitchenpos.stub;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

import static kitchenpos.stub.MenuStub.generateFiveThousandMenuProductsPriceInVisibleSamePriceMenu;
import static kitchenpos.stub.MenuStub.generateFiveThousandMenuProductsPriceVisibleSamePriceMenu;

public class OrderLineItemStub {

    private OrderLineItemStub() {
    }

    public static OrderLineItem generateFiveThousandPriceMenuTwoQuantityOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = generateFiveThousandMenuProductsPriceVisibleSamePriceMenu();
        long quantity  = 2L;
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public static OrderLineItem generateNegativeQuantityOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = generateFiveThousandMenuProductsPriceVisibleSamePriceMenu();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(-1);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public static OrderLineItem generateInvisibleMenuOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = generateFiveThousandMenuProductsPriceInVisibleSamePriceMenu();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public static OrderLineItem generateDifferentPriceThanMenuOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = generateFiveThousandMenuProductsPriceVisibleSamePriceMenu();
        long quantity  = 2L;
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(BigDecimal.valueOf(1000));
        return orderLineItem;
    }
}
