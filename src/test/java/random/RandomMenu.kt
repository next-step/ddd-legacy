package random

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuProduct
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

fun randomMenu(
    id: UUID = UUID.randomUUID(),
    name: String? = randomMenuName(),
    price: BigDecimal? = randomPrice(),
    menuGroup: MenuGroup = randomMenuGroup(),
    isDisplayed: Boolean = Random.nextBoolean(),
    menuProducts: List<MenuProduct> = emptyList(),
    menuGroupId: UUID = UUID.randomUUID()
): Menu {
    return Menu().apply {
        this.id = id
        this.name = name
        this.price = price
        this.menuGroup = menuGroup
        this.isDisplayed = isDisplayed
        this.menuProducts = menuProducts
        this.menuGroupId = menuGroupId
    }
}

fun randomMenuId() = UUID.randomUUID()

fun randomMenuName() = listOf("후라이드치킨", "양념치킨", "순살양념", "감자튀김", "양념반후라이드반", "허니순살", "허니콤보").random()

fun randomMenuGroup(
    id: UUID = UUID.randomUUID(),
    name: String? = randomMenuGroupName()
): MenuGroup {
    return MenuGroup().apply {
        this.id = id
        this.name = name
    }
}

fun randomMenuGroupName(): String = listOf("오리지널 시리즈", "사이드 메뉴", "어린이 메뉴", "허니 시리즈", "레드 시리즈").random()
