package kitchenpos.fixture

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Order
import kitchenpos.domain.OrderLineItem
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderType
import kitchenpos.domain.Product
import kitchenpos.utils.generateUUIDFrom
import java.math.BigDecimal
import java.util.UUID

// menu-group
val EXISTING_MENU_GROUP_ID = generateUUIDFrom("f1860abc2ea1411bbd4abaa44f0d5580")

// menu
val EXISTING_MENU_ID = generateUUIDFrom("b9c670b04ef5409083496868df1c7d62")
val NOT_EXISTING_MENU_GROUP_ID = generateUUIDFrom("f1860abc2ea1411bbd4abaa44f0d1111")
val NON_DISPLAYED_MENU_ID = generateUUIDFrom("33e558df7d934622b50efcc4282cd184")

// order-table
val EXISTING_ORDER_TABLE_ID = generateUUIDFrom("8d71004329b6420e8452233f5a035520")

// product
val EXISTING_PRODUCT_ID_1 = generateUUIDFrom("3b52824434f7406bbb7e690912f66b10")
val EXISTING_PRODUCT_ID_2 = generateUUIDFrom("c5ee925c3dbb4941b825021446f24446")

fun initOrderTable(
    id: UUID = EXISTING_ORDER_TABLE_ID,
    name: String = "1번 테이블",
    numberOfGuests: Int = 0,
    isOccupied: Boolean = false,
): OrderTable {
    return OrderTable().apply {
        this.id = id
        this.name = name
        this.numberOfGuests = numberOfGuests
        this.isOccupied = isOccupied
    }
}

fun initMenu(
    id: UUID? = null,
    name: String = "후라이드 치킨",
    price: BigDecimal = BigDecimal.valueOf(16_000),
    isDisplayed: Boolean = true,
    menuGroupId: UUID? = EXISTING_MENU_GROUP_ID,
    menuProducts: List<MenuProduct> = listOf(),
): Menu {
    return Menu().apply {
        this.id = id
        this.name = name
        this.price = price
        this.menuGroupId = menuGroupId
        this.menuProducts = menuProducts
        this.isDisplayed = isDisplayed
    }
}

fun initMenuProduct(
    productId: UUID = EXISTING_PRODUCT_ID_1,
    quantity: Long = 1L,
): MenuProduct {
    return MenuProduct().apply {
        this.productId = productId
        this.quantity = quantity
    }
}

fun initProduct(
    id: UUID? = null,
    name: String? = "후라이드 치킨",
    price: BigDecimal = BigDecimal.valueOf(16_000),
): Product {
    return Product().apply {
        this.id = id
        this.name = name
        this.price = price
    }
}

fun initOrderLineItem(
    menuId: UUID = EXISTING_MENU_ID,
    quantity: Long = 1,
    price: BigDecimal = BigDecimal.valueOf(17_000),
): OrderLineItem {
    return OrderLineItem().apply {
        this.menuId = menuId
        this.quantity = quantity
        this.price = price
    }
}

fun initEatInOrder(
    type: OrderType = OrderType.EAT_IN,
    orderTableId: UUID = EXISTING_ORDER_TABLE_ID,
    orderLineItems: List<OrderLineItem> = listOf(initOrderLineItem()),
): Order {
    return Order().apply {
        this.type = type
        this.orderTableId = orderTableId
        this.orderLineItems = orderLineItems
    }
}

fun initDeliveryOrder(
    type: OrderType = OrderType.DELIVERY,
    deliveryAddress: String = "nextstep 사무실",
    orderLineItems: List<OrderLineItem> = listOf(initOrderLineItem()),
): Order {
    return Order().apply {
        this.type = type
        this.deliveryAddress = deliveryAddress
        this.orderLineItems = orderLineItems
    }
}

fun initTakeoutOrder(
    type: OrderType = OrderType.TAKEOUT,
    orderLineItems: List<OrderLineItem> = listOf(initOrderLineItem()),
): Order {
    return Order().apply {
        this.type = type
        this.orderLineItems = orderLineItems
    }
}
