package kitchenpos.testsupport

import java.math.BigDecimal
import java.util.UUID
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product

object MenuFixtures {
    fun createMenu(
        product: Product,
        price: BigDecimal = product.price,
        isDisplayed: Boolean = true,
        name: String? = "test-menu-name"
    ): Menu {
        return Menu().apply {
            this.id = UUID.randomUUID()
            this.price = price
            this.name = name
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