package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeMenuRepository : InMemoryRepository<Menu>(), MenuRepository {
    override fun findAllByIdIn(ids: MutableList<UUID>?): MutableList<Menu> {
        return items.filter { it.id in (ids ?: emptyList()) }.toMutableList()
    }

    override fun findAllByProductId(productId: UUID?): MutableList<Menu> {
        return items.filter { menu -> menu.menuProducts.any { it.productId == productId } }.toMutableList()
    }

    override fun findById(id: UUID): Optional<Menu>? {
        return items.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }
}
