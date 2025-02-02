package kitchenpos.application

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class CreateMenuHelper(
    private val menuRepository: MenuRepository,
    private val menuGroupRepository: MenuGroupRepository,
    private val productRepository: ProductRepository
) {
    fun createTestMenu(
        menuName: String = "메뉴",
        menuPrice: BigDecimal = BigDecimal("1000"),
        menuGroupName: String = "메뉴 그룹",
        productName: String = "상품",
        productPrice: BigDecimal = BigDecimal("1000"),
        productQuantity: Long = 1L,
        isDisplayed: Boolean = false
    ): Menu {
        val menuGroup = menuGroupRepository.save(
            MenuGroup().apply {
                id = UUID.randomUUID()  // ID 할당 추가
                name = menuGroupName
            }
        )

        val product = productRepository.save(
            Product().apply {
                id = UUID.randomUUID()  // ID 할당 추가
                name = productName
                price = productPrice
            }
        )

        val menuProduct = MenuProduct().apply {
            this.product = product
            quantity = productQuantity
        }

        return menuRepository.save(
            Menu().apply {
                id = UUID.randomUUID()  // ID 할당 추가
                name = menuName
                price = menuPrice
                this.menuGroup = menuGroup
                menuProducts = listOf(menuProduct)
                this.isDisplayed = isDisplayed
            }
        )
    }
}
