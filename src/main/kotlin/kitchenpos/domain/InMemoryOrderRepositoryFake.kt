package kitchenpos.domain

import java.util.*

class InMemoryOrderRepositoryFake : OrderRepository {
    private val orders: MutableMap<UUID, Order> = mutableMapOf()

    override fun save(order: Order): Order {
        orders[order.id] = order
        return order
    }

    override fun findById(id: UUID): Optional<Order> {
        return Optional.ofNullable(orders[id])
    }

    override fun findAll(): List<Order> {
        return orders.values.toList()
    }

    override fun existsByOrderTableAndStatusNot(orderTable: OrderTable?, status: OrderStatus): Boolean {
        return orders[orderTable?.id]?.status != status
    }

}
