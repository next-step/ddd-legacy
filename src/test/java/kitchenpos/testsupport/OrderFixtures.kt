package kitchenpos.testsupport

import io.kotest.assertions.eq.isOrderedSet
import java.math.BigDecimal
import java.util.UUID
import kitchenpos.domain.Menu
import kitchenpos.domain.Order
import kitchenpos.domain.OrderLineItem
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderType

object OrderFixtures {
    fun createOrder(
        type: OrderType,
        status: OrderStatus,
        menus: List<Menu>,
        orderLineItemRequestPrice: BigDecimal? = null,
        quantity: Long = 1,
        deliveryAddress: String? = null,
        orderTable: OrderTable? = null
    ): Order {
        return Order().apply {
            this.id = UUID.randomUUID()
            this.type = type
            this.status = status
            this.orderLineItems = menus.map { menu ->
                OrderLineItem().also {
                    it.menu = menu
                    it.menuId = menu.id
                    it.price = orderLineItemRequestPrice ?: menu.price
                    it.quantity = quantity
                }
            }
            this.deliveryAddress = deliveryAddress
            this.orderTableId = orderTable?.id
            this.orderTable = orderTable
        }
    }
}