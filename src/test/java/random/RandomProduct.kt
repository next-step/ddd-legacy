package random

import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random
import kotlin.random.nextLong


fun randomProduct(
    id: UUID = randomProductId(),
    name: String? = randomProductName(),
    price: BigDecimal? = randomPrice(),
): Product {
    return Product().apply {
        this.id = id
        this.name = name
        this.price = price
    }
}

fun randomProductId() = UUID.randomUUID()

fun randomProductName(): String {
    return listOf("후라이드치킨", "양념치킨", "후라이드순살치킨", "양념순살치킨").random()
}

fun randomMenuProduct(
    seq: Long = Random.nextLong(0, Long.MAX_VALUE),
    product: Product = randomProduct(),
    quantity: Long = Random.nextLong(0, Long.MAX_VALUE),
    productId: UUID = randomProductId()
): MenuProduct {
    return MenuProduct().apply {
        this.seq = seq
        this.product = product
        this.quantity = quantity
        this.productId = productId
    }
}
