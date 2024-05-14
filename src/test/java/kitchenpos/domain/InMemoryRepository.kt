package kitchenpos.domain

import java.util.UUID

abstract class InMemoryRepository<T> : BaseRepository<T, UUID> {
    protected val items = mutableListOf<T>()

    fun clear() = items.clear()

    override fun <S : T?> save(entity: S): S {
        entity?.let { items.add(it) }
        return entity
    }

    override fun findAll(): MutableList<T> {
        return items
    }
}
