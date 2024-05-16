package domain

import domain.ProductFixtures.makeProductOne
import kitchenpos.domain.MenuProduct

object MenuProductFixtures {
    fun makeMenuProductOne(): MenuProduct {
        return MenuProduct().apply {
            this.seq = 1
            this.product = makeProductOne()
            this.quantity = 1L
            this.productId = this.product.id
        }
    }
}
