package kitchenpos.menu

import kitchenpos.domain.Menu

class MenuFixture {
    companion object {
        private const val DEFAULT_NAME: String = "테스트 메뉴"
        private const val DEFAULT_PRICE: Int = 10000

        fun fixture(
            name: String? = DEFAULT_NAME,
            price: Int = DEFAULT_PRICE,
        ): Menu {
            val menu = Menu()
            menu.name = name
            menu.price = price.toBigDecimal()
            return menu
        }
    }
}
