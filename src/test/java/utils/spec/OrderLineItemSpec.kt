package utils.spec

import kitchenpos.domain.OrderLineItem

object OrderLineItemSpec {
    fun of(): OrderLineItem {
        val orderLineItem = OrderLineItem()

        val menu = MenuSpec.of()

        orderLineItem.seq = 1000
        orderLineItem.quantity = 1
        orderLineItem.price = MenuSpec.of().price
        orderLineItem.menuId = menu.id
        orderLineItem.menu = menu

        return orderLineItem
    }
}
