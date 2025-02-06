package kitchenpos.order

import kitchenpos.domain.Menu
import kitchenpos.domain.Order
import kitchenpos.domain.OrderLineItem
import java.math.BigDecimal

class OrderLineItemFixture {
    companion object {
        fun fixture(
            order: Order,
            menu: Menu,
            quantity: Long,
        ): OrderLineItem {
            val orderLineItem = OrderLineItem()
            orderLineItem.seq = order.orderLineItems.size.toLong()
            orderLineItem.menu = menu
            orderLineItem.quantity = quantity
            orderLineItem.menuId = menu.id
            orderLineItem.price = menu.price.multiply(BigDecimal.valueOf(quantity))
            return orderLineItem
        }
    }
}
