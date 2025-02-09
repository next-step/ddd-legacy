package random

import kitchenpos.domain.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

fun randomOrder(
    id: UUID = UUID.randomUUID(),
    type: OrderType? = OrderType.entries.random(),
    status: OrderStatus = OrderStatus.entries.random(),
    orderDateTime: LocalDateTime = LocalDateTime.now(),
    orderLineItems: List<OrderLineItem> = emptyList(),
    deliveryAddress: String? = randomDeliveryAddress(),
    orderTable: OrderTable = randomOrderTable(),
    orderTableId: UUID = UUID.randomUUID()
): Order {
    return Order().apply {
        this.id = id
        this.type = type
        this.status = status
        this.orderDateTime = orderDateTime
        this.orderLineItems = orderLineItems
        this.deliveryAddress = deliveryAddress
        this.orderTable = orderTable
        this.orderTableId = orderTableId
    }
}

fun randomOrderId() = UUID.randomUUID()

fun randomDeliveryAddress() = listOf("서울", "부산", "대구", "울산", "제주도").random()

fun randomOrderLineItem(
    seq: Long = Random.nextLong(0, Long.MAX_VALUE),
    menu: Menu = randomMenu(),
    quantity: Long = Random.nextLong(0, Long.MAX_VALUE),
    menuId: UUID = UUID.randomUUID(),
    price: BigDecimal = randomPrice()
): OrderLineItem {
    return OrderLineItem().apply {
        this.seq = seq
        this.menu = menu
        this.quantity = quantity
        this.menuId = menuId
        this.price = price
    }
}
