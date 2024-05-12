package domain

import domain.OrderLineItemFixtures.makeOrderLineItemOne
import kitchenpos.domain.Order
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderType
import java.time.LocalDateTime
import java.util.*

object OrderFixtures {
    fun makeOrderOne(): Order {
        return Order().apply {
            this.id = UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8")
            this.type = OrderType.DELIVERY
            this.status = OrderStatus.SERVED
            this.deliveryAddress = "서울시 강남구"
            this.orderLineItems = listOf(makeOrderLineItemOne())
            this.orderDateTime = LocalDateTime.now()
        }
    }

    fun makeOrderTwo(): Order {
        return Order().apply {
            this.id = UUID.fromString("4519efc6-91af-446c-b54f-f1f5e4d11201")
            this.type = OrderType.EAT_IN
            this.status = OrderStatus.COMPLETED
            this.orderTable = OrderTableFixtures.makeOrderTableOne()
            this.orderLineItems = listOf(makeOrderLineItemOne())
            this.orderDateTime = LocalDateTime.now()
        }
    }
}
