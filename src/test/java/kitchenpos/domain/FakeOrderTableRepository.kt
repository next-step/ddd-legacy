package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeOrderTableRepository : OrderTableRepository {
    private val orderTables = mutableListOf<OrderTable>()

    override fun <S : OrderTable?> save(entity: S): S {
        entity?.let { orderTables.add(it) }
        return entity
    }

    override fun findById(id: UUID?): Optional<OrderTable>? {
        return orderTables.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun findAll(): MutableList<OrderTable> {
        return orderTables
    }
}
