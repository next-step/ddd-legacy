package utils

import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import java.util.*

object FakeProductRepository : ProductRepository {
    private val productMap = mutableMapOf<UUID, Product>()

    override fun findAllByIdIn(ids: List<UUID>): List<Product> {
        return ids.mapNotNull { productMap[it] }
    }

    override fun findById(productId: UUID): Optional<Product> {
        return Optional.ofNullable(productMap[productId])
    }

    override fun findAll(): List<Product> {
        return productMap.values.toList()
    }

    override fun save(product: Product): Product {
        productMap[product.id] = product

        return product
    }
}
