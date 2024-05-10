package utils.spec

import kitchenpos.domain.Product
import java.math.BigDecimal
import java.util.UUID

object ProductSpec {
    fun of(price: BigDecimal = BigDecimal.valueOf(10000)): Product {
        val product = Product()

        product.id = UUID.randomUUID()
        product.price = price
        product.name = "test product"

        return product
    }
}
