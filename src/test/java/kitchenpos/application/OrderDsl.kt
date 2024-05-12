package kitchenpos.application

import kitchenpos.domain.Menu
import kitchenpos.domain.Order
import kitchenpos.domain.OrderLineItem
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderType
import java.util.UUID

fun buildOrder(initializer: Order.() -> Unit): Order {
    return Order().apply {
        id = UUID.randomUUID()
        initializer()
    }
}

fun Order.orderType(type: OrderType) {
    this.type = type
}

fun Order.deliveryAddress(address: String?) {
    this.deliveryAddress = address
}

fun Order.orderStatus(status: OrderStatus) {
    this.status = status
}

fun Order.requestMenu(initializer: MutableList<OrderLineItem>.() -> Unit) {
    this.orderLineItems = mutableListOf<OrderLineItem>().apply(initializer)
}

fun Order.table(
    occupied: Boolean = false,
    name: String? = "테이블",
    numberOfGuest: Int = 0
) {
    this.orderTable =
        OrderTable().apply {
            this.id = UUID.randomUUID()
            this.numberOfGuests = numberOfGuest
            this.isOccupied = occupied
        }
    this.orderTableId = this.orderTable.id
}

fun MutableList<OrderLineItem>.item(
    name: String,
    price: Int,
    quantity: Long,
    isDisplayed: Boolean = true
) {
    val newSeq = this.size.toLong() + 1
    this.add(
        OrderLineItem().apply {
            this.menu =
                Menu().apply {
                    this.id = UUID.randomUUID()
                    this.name = name
                    this.price = price.toBigDecimal()
                    this.isDisplayed = isDisplayed
                }
            this.menuId = this.menu.id
            this.price = this.menu.price
            this.quantity = quantity
            this.seq = newSeq
        }
    )
}
