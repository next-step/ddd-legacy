package utils.spec

import kitchenpos.domain.*
import java.util.*

object OrderSpec {
    fun of(
        orderLineItems: List<OrderLineItem> = listOf(OrderLineItemSpec.of()),
        type: OrderType = OrderType.DELIVERY,
        status: OrderStatus = OrderStatus.WAITING,
        orderTable: OrderTable? = null
    ): Order {
        val order = Order()

        order.id = UUID.randomUUID()
        order.type = type
        order.status = status
        order.orderLineItems = orderLineItems

        if (type == OrderType.DELIVERY) {
            order.deliveryAddress = "test delivery"
        }

        if (type == OrderType.EAT_IN) {
            order.orderTable = orderTable
            order.orderTableId = order.orderTable.id
        }

        return order
    }
}
