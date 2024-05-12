package kitchenpos.dsl

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product
import java.math.BigDecimal
import java.util.UUID

fun menu(
    name: String,
    price: Int,
    initializer: Menu.() -> Unit
): Menu {
    return Menu().apply(initializer).apply {
        this.id = UUID.randomUUID()
        this.name = name
        this.price = BigDecimal(price)
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

    operator fun String.invoke(initializer: ProductItem.() -> Unit) {
        val productItem = ProductItem(this).apply(initializer)
        val newProduct =
            Product().apply {
                this.id = UUID.randomUUID()
                this.name = productItem.name
                this.price = productItem.price
            }

        items.add(
            MenuProduct().apply {
                this.product = newProduct
                this.productId = newProduct.id
                this.seq = productItem.seq
            }
        )
    }
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
    var seq: Long = 0

    infix fun seq(seq: Long): ProductItem {
        this.seq = seq
        return this
    }
}
