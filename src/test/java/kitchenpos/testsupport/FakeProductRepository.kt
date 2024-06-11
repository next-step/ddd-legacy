package kitchenpos.testsupport

import java.util.Optional
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository

class FakeProductRepository : ProductRepository {
    private val storage: MutableMap<UUID, Product> = ConcurrentHashMap()

    override fun findAllByIdIn(list: MutableList<UUID>): MutableList<Product> {
        return storage.values
            .filter { it.id in list }
            .toMutableList()
    }

    override fun findById(productId: UUID): Optional<Product> {
        return Optional.ofNullable(storage[productId])
    }

    override fun findAll(): MutableList<Product> {
        return storage.values.toMutableList()
    }

    override fun save(product: Product): Product {
        storage[product.id] = product
        return product
    }
}