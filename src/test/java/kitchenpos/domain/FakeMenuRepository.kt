package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeMenuRepository : MenuRepository {
    private val menus = mutableListOf<Menu>()

    override fun <S : Menu?> save(entity: S): S {
        entity?.let { menus.add(it) }
        return entity
    }

    override fun findById(id: UUID?): Optional<Menu>? {
        return menus.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun findAll(): MutableList<Menu> {
        return menus
    }

    override fun findAllByIdIn(ids: MutableList<UUID>?): MutableList<Menu> {
        return menus.filter { it.id in (ids ?: emptyList()) }.toMutableList()
    }

    override fun findAllByProductId(productId: UUID?): MutableList<Menu> {
        return menus.filter { menu -> menu.menuProducts.any { it.productId == productId } }.toMutableList()
    }
}
