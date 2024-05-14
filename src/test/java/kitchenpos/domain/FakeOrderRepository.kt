package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeOrderRepository : InMemoryRepository<Order>(), OrderRepository {
    override fun findById(id: UUID): Optional<Order>? {
        return items.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun existsByOrderTableAndStatusNot(
        orderTable: OrderTable?,
        status: OrderStatus?
    ): Boolean {
        return items.any { it.orderTableId == orderTable?.id && it.status != status }
    }
}
