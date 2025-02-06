package kitchenpos.menu

import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product

class MenuProductFixture {
    companion object {
        private const val DEFAULT_QUANTITY: Long = 1

        fun fixture(
            seq: Long,
            product: Product,
            quantity: Long = DEFAULT_QUANTITY,
        ): MenuProduct {
            val menuProduct = MenuProduct()
            menuProduct.seq = seq
            menuProduct.productId = product.id
            menuProduct.quantity = quantity
            return menuProduct
        }
    }
}
