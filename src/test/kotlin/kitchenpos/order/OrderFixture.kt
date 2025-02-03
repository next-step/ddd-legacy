package kitchenpos.order

import kitchenpos.domain.Order
import kitchenpos.domain.OrderLineItem
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderType
import java.time.LocalDateTime

class OrderFixture {
    companion object {
        fun fixture(
            type: OrderType = OrderType.TAKEOUT,
            status: OrderStatus = OrderStatus.WAITING,
            orderLineItems: List<OrderLineItem> = mutableListOf()
        ): Order {
            val order = Order()
            order.type = type
            order.status = status
            order.orderDateTime = LocalDateTime.now()
            order.orderLineItems = orderLineItems
            return order
        }
    }
}
