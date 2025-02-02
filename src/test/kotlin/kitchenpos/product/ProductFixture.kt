package kitchenpos.product

import io.cucumber.java.ParameterType
import kitchenpos.domain.Product
import java.math.BigDecimal

@ParameterType(".*")
fun productName(name: String?): String? {
    if (name == "null") {
        return null
    } else {
        return name
    }
}


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

