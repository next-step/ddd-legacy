package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeMenuGroupRepository : MenuGroupRepository {
    private val menuGroups = mutableListOf<MenuGroup>()

    override fun <S : MenuGroup?> save(entity: S): S {
        entity?.let { menuGroups.add(it) }
        return entity
    }

    override fun findById(id: UUID): Optional<MenuGroup> {
        return menuGroups.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun findAll(): MutableList<MenuGroup> {
        return menuGroups
    }

    fun clear() = menuGroups.clear()
}
