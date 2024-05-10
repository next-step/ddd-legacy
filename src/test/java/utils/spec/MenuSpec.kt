package utils.spec

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import java.math.BigDecimal
import java.util.UUID

object MenuSpec {
    fun of(menuProducts: List<MenuProduct>, price: BigDecimal, display: Boolean = true): Menu {
        val menu = Menu()

        menu.id = UUID.randomUUID()
        menu.menuGroup = MenuGroupSpec.of()
        menu.menuGroupId = menu.menuGroup.id
        menu.isDisplayed = display
        menu.name = "test menu"
        menu.price = price
        menu.menuProducts = menuProducts

        return menu
    }
}
