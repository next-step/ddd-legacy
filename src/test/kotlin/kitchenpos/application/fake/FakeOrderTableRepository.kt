package kitchenpos.application.fake

import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import java.util.*

class FakeOrderTableRepository : OrderTableRepository {
    private val orderTables = mutableMapOf<UUID, OrderTable>()

    override fun save(entity: OrderTable?): OrderTable {
        orderTables[entity!!.id] = entity
        return entity
    }

    override fun findById(id: UUID?): Optional<OrderTable> {
        return Optional.ofNullable(orderTables[id])
    }

    override fun findAll(): MutableList<OrderTable> {
        return orderTables.values.toMutableList()
    }
}
