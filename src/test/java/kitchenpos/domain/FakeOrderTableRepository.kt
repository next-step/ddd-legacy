package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeOrderTableRepository : InMemoryRepository<OrderTable>(), OrderTableRepository {
    override fun findById(id: UUID): Optional<OrderTable> {
        return items.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }
}
