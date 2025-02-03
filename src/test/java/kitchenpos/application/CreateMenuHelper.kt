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
        isDisplayed: Boolean = false,
        products: List<ProductDto> = listOf(
            ProductDto(
                name = "상품",
                price = BigDecimal("1000"),
                quantity = 1L
            )
        )
    ): Menu {
        val menuGroup = menuGroupRepository.save(
            MenuGroup().apply {
                id = UUID.randomUUID()  // ID 할당 추가
                name = menuGroupName
            }
        )

        val savedProducts = products.map { productDto ->
            if (productDto.id != null) {
                return@map productRepository.findById(productDto.id).orElseThrow()
            }
            productRepository.save(
                Product().apply {
                    id = UUID.randomUUID()  // ID 할당 추가
                    name = productDto.name
                    price = productDto.price
                }
            )
        }

        val menuProducts = savedProducts.map { savedProduct ->
            MenuProduct().apply {
                this.product = savedProduct
                quantity = products.find { it.name == savedProduct.name }?.quantity ?: 1L
            }
        }

        val menu = Menu().apply {
            id = UUID.randomUUID()  // ID 할당 추가
            name = menuName
            price = menuPrice
            this.menuGroup = menuGroup
            this.menuProducts = menuProducts
            this.isDisplayed = isDisplayed
        }
        return menuRepository.save(menu)
    }

    data class ProductDto(
        val id: UUID? = null,
        val name: String,
        val price: BigDecimal,
        val quantity: Long,
    )
}
