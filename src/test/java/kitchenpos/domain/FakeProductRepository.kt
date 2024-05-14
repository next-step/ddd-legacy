package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeProductRepository : InMemoryRepository<Product>(), ProductRepository {
    override fun findById(id: UUID): Optional<Product> {
        return items.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun findAllByIdIn(ids: MutableList<UUID>): MutableList<Product> {
        return items.filter { it.id in ids }.toMutableList()
    }
}
