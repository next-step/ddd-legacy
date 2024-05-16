package domain

import kitchenpos.domain.OrderLineItem

object OrderLineItemFixtures {
    fun makeOrderLineItemOne(): OrderLineItem {
        return OrderLineItem().apply {
            val menu = MenuFixtures.makeMenuOne()
            this.seq = 1
            this.menu = menu
            this.quantity = 1L
            this.menuId = menu.id
            this.price = menu.price
        }
    }
}
