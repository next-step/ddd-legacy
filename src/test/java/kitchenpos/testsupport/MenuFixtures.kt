package kitchenpos.testsupport

import java.math.BigDecimal
import java.util.UUID
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product

object MenuFixtures {
    fun createMenu(
        price: BigDecimal = 1000.toBigDecimal(),
        isDisplayed: Boolean = true,
        product: Product
    ): Menu {
        return Menu().apply {
            this.id = UUID.randomUUID()
            this.price = price
            this.isDisplayed = isDisplayed
            this.menuProducts = listOf(
                MenuProduct().apply {
                    this.product = product
                    this.quantity = 1
                }
            )
        }
    }
}