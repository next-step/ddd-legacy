package kitchenpos.domain

import java.util.Optional
import java.util.UUID

class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()

    override fun <S : Product?> save(entity: S): S {
        entity?.let { products.add(it) }
        return entity
    }

    override fun findById(id: UUID?): Optional<Product>? {
        return products.find { it.id == id }?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun findAll(): MutableList<Product> {
        return products
    }

    override fun findAllByIdIn(ids: MutableList<UUID>?): MutableList<Product> {
        return products.filter { it.id in (ids ?: emptyList()) }.toMutableList()
    }

    fun clear() = products.clear()
}
