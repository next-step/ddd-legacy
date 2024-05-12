package kitchenpos.application

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product
import java.math.BigDecimal
import java.util.UUID

fun buildMenu(initializer: Menu.() -> Unit): Menu {
    return Menu().apply(initializer).apply {
        this.id = UUID.randomUUID()
    }
}

fun Menu.groupName(name: String) {
    val newMenuGroup =
        MenuGroup().apply {
            this.id = UUID.randomUUID()
            this.name = name
        }

    this.menuGroup = newMenuGroup
    this.menuGroupId = newMenuGroup.id
}

fun Menu.products(initializer: Products.() -> Unit) {
    this.menuProducts = Products().apply(initializer).items
}

data class Products(val items: MutableList<MenuProduct> = mutableListOf()) {
    fun item(
        name: String,
        price: BigDecimal,
        quantity: Long
    ) {
        val newSeq = this.items.size.toLong() + 1
        this.items.add(
            MenuProduct().apply {
                this.product =
                    Product().apply {
                        this.id = UUID.randomUUID()
                        this.name = name
                        this.price = price
                    }
                this.quantity = quantity
                this.seq = newSeq
            }
        )
    }
}
