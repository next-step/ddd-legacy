package kitchenpos.domain

import java.util.*

interface ProductRepository {
    fun findAllByIdIn(ids: List<UUID>): List<Product>
    fun findById(productId: UUID): Optional<Product>
    fun findAll(): List<Product>
    fun save(product: Product): Product
}
