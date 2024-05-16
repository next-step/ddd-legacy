package kitchenpos.application.fake

import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import java.util.*

class FakeProductRepository : ProductRepository {
    private val products = mutableMapOf<UUID, Product>()

    override fun save(entity: Product?): Product {
        products[entity!!.id] = entity
        return entity
    }

    override fun findById(id: UUID): Optional<Product> {
        return Optional.ofNullable(products[id])
    }

    override fun findAll(): MutableList<Product> {
        return products.values.toMutableList()
    }

    override fun findAllByIdIn(ids: MutableList<UUID>?): MutableList<Product> {
        return products.filter {
            it.key in ids!!
        }.map {
            it.value
        }.toMutableList()
    }
}
