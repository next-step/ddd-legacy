package kitchenpos.domain

import java.util.*

class InMemoryOrderTableRepositoryFake : OrderTableRepository {
    private val orderTables: MutableMap<UUID, OrderTable> = mutableMapOf()

    override fun save(orderTable: OrderTable): OrderTable {
        orderTables[orderTable.id] = orderTable
        return orderTable
    }

    override fun findById(id: UUID): Optional<OrderTable> {
        return Optional.ofNullable(orderTables[id])
    }

    override fun findAll(): List<OrderTable> {
        return orderTables.values.toList()
    }
}
