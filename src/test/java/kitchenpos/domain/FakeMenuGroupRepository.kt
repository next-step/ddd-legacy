package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeMenuGroupRepository : InMemoryRepository<MenuGroup>(), MenuGroupRepository {
    override fun findById(id: UUID): Optional<MenuGroup> {
        return items.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }
}
