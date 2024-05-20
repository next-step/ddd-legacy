package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.mockk
import java.math.BigDecimal
import java.util.UUID
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeProductRepository
import kitchenpos.testsupport.MenuFixtures.createMenu
import kitchenpos.testsupport.ProductFixtures.createProduct

class ProductServiceChangePriceTest : ShouldSpec({
    lateinit var savedProduct: Product
    lateinit var savedMenu: Menu

    lateinit var productRepository: ProductRepository
    lateinit var menuRepository: MenuRepository
    lateinit var service: ProductService

    beforeTest {
        productRepository = FakeProductRepository()
        menuRepository = FakeMenuRepository()

        service = ProductService(
            productRepository,
            menuRepository,
            mockk()
        )

        savedProduct = createProduct()
        productRepository.save(savedProduct)

        savedMenu = createMenu(
            price = savedProduct.price,
            product = savedProduct
        )
        menuRepository.save(savedMenu)
    }

    context("상품 가격 수정") {
        should("성공") {
            // given
            val productId = savedProduct.id
            val request = createRequest(
                price = 2000.toBigDecimal()
            )

            // when
            val changedProduct = service.changePrice(
                productId,
                request
            )

            // then
            changedProduct.id shouldBe productId
            changedProduct.price shouldBe request.price
        }

        should("성공 - 메뉴의 가격이 메뉴 상품들의 가격 합보다 큰 경우 메뉴는 비노출 처리가 된다") {
            // given
            val productId = savedProduct.id
            val request = createRequest(
                price = 1.toBigDecimal()
            )

            // when
            val changedProduct = service.changePrice(
                productId,
                request
            )

            // then
            val menus = menuRepository.findAllByProductId(changedProduct.id)

            changedProduct.id shouldBe productId
            changedProduct.price shouldBe request.price
            menus.shouldForAll { menu ->
                menu.isDisplayed shouldBe false
            }
        }

        should("실패 - 상품 가격이 0원 미만인 경우") {
            // given
            val productId = savedProduct.id
            val request = createRequest(
                price = (-1000).toBigDecimal()
            )

            // when
            val exception = shouldThrowAny {
                service.changePrice(
                    productId,
                    request
                )
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }
    }
}) {
    companion object {
        private fun createRequest(
            price: BigDecimal
        ): Product {
            return createProduct(
                price = price
            )
        }
    }
}