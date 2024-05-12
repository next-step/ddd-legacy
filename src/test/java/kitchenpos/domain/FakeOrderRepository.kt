package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeOrderRepository : OrderRepository {
    private val orders = mutableListOf<Order>()

    override fun <S : Order?> save(entity: S): S {
        entity?.let { orders.add(it) }
        return entity
    }

    override fun findById(id: UUID?): Optional<Order>? {
        return orders.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun findAll(): MutableList<Order> {
        return orders
    }

    override fun existsByOrderTableAndStatusNot(
        orderTable: OrderTable?,
        status: OrderStatus?
    ): Boolean {
        return orders.any { it.orderTable == orderTable && it.status != status }
    }
}
