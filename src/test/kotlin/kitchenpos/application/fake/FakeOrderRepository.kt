package kitchenpos.application.fake

import kitchenpos.domain.Order
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTable
import java.util.*

class FakeOrderRepository : OrderRepository {
    private val orders = mutableMapOf<UUID, Order>()

    override fun existsByOrderTableAndStatusNot(
        orderTable: OrderTable,
        status: OrderStatus?,
    ): Boolean {
        return orders.any {
            it.value.orderTable?.id == orderTable.id && it.value.status != status
        }
    }

    override fun save(order: Order): Order {
        orders[order.id] = order
        return order
    }

    override fun findById(id: UUID?): Optional<Order> {
        return Optional.ofNullable(orders[id])
    }

    override fun findAll(): MutableList<Order> {
        return orders.values.toMutableList()
    }
}
