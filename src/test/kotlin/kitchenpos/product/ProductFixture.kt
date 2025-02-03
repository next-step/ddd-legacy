package kitchenpos.product

import kitchenpos.domain.Product
import java.math.BigDecimal


class ProductFixture {

    companion object {
        private const val DEFAULT_NAME: String = "테스트 상품"
        private val DEFAULT_PRICE: BigDecimal = BigDecimal.valueOf(1000L)

        fun fixture(
            name: String? = DEFAULT_NAME,
            price: BigDecimal? = DEFAULT_PRICE
        ): Product {
            val product = Product()
            product.name = name
            product.price = price
            return product
        }
    }
}

