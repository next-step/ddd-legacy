package utils.spec

import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product

object MenuProductSpec {
    fun of(product: Product = ProductSpec.of(), quantity: Long = 1): MenuProduct {
        val menuProduct = MenuProduct()

        menuProduct.productId = product.id
        menuProduct.product = product
        menuProduct.quantity = quantity

        return menuProduct
    }
}
