package domain

import domain.OrderLineItemFixtures.makeOrderLineItemOne
import kitchenpos.domain.Order
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderType
import java.util.*

object OrderFixtures {
    fun makeOrderOne(): Order {
        return Order().apply {
            this.id = UUID.randomUUID()
            this.type = OrderType.DELIVERY
            this.status = OrderStatus.SERVED
            this.deliveryAddress = "서울 특별시 양천구 목동"
            this.orderTable = OrderTableFixtures.makeOrderTableOne()
            this.orderLineItems = listOf(makeOrderLineItemOne())
        }
    }
}
