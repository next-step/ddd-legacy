package kitchenpos.dsl

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product
import java.math.BigDecimal
import java.util.UUID

fun menu(
    name: String?,
    price: Int?,
    initializer: Menu.() -> Unit
): Menu {
    return Menu().apply(initializer).apply {
        this.id = UUID.randomUUID()
        this.name = name
        this.price = price?.toBigDecimal()
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

class Products {
    val items = mutableListOf<MenuProduct>()
}

fun Products.item(item: ProductItem) {
    val newSeq = this.items.size.toLong() + 1
    this.items.add(
        MenuProduct().apply {
            this.product =
                Product().apply {
                    this.id = UUID.randomUUID()
                    this.name = item.name
                    this.price = item.price
                }
            this.quantity = item.quantity
            this.seq = newSeq
        }
    )
}

infix fun String.price(price: Int): ProductItem {
    return ProductItem(this) price price
}

infix fun ProductItem.price(price: Int): ProductItem {
    this.price = BigDecimal(price)
    return this
}

class ProductItem(val name: String) {
    var price: BigDecimal = BigDecimal.ZERO
    var quantity: Long = 0

    infix fun quantity(quantity: Long): ProductItem {
        this.quantity = quantity
        return this
    }
}
