package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import kitchenpos.testsupport.FakeMenuGroupRepository
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeProductRepository
import kitchenpos.testsupport.MenuFixtures
import kitchenpos.testsupport.ProductFixtures

class MenuServiceChangePriceTest : ShouldSpec({
    lateinit var menuRepository: MenuRepository
    lateinit var menuGroupRepository: MenuGroupRepository
    lateinit var productRepository: ProductRepository
    lateinit var purgomalumClient: PurgomalumClient
    lateinit var service: MenuService

    lateinit var savedMenu: Menu

    beforeTest {
        menuRepository = FakeMenuRepository()
        menuGroupRepository = FakeMenuGroupRepository()
        productRepository = FakeProductRepository()
        purgomalumClient = mockk {
            every { containsProfanity(any()) } returns false
        }


        savedMenu = menuRepository.save(
            MenuFixtures.createMenu(
                product = ProductFixtures.createProduct()
            )
        )

        service = MenuService(
            menuRepository,
            menuGroupRepository,
            productRepository,
            purgomalumClient,
        )
    }

    context("메뉴 수정") {
        should("성공") {
            // given
            val menuId = savedMenu.id
            val price = savedMenu.price - 1.toBigDecimal()
            val request = createRequest(
                price = price
            )

            // when
            val changedMenu = service.changePrice(
                menuId,
                request
            )

            // then
            changedMenu.id shouldBe menuId
            changedMenu.price shouldBe price
            changedMenu.isDisplayed shouldBe true
        }

        should("실패 - 메뉴 가격이 0원 미만인 경우") {
            // given
            val menuId = savedMenu.id
            val minusPrice = savedMenu.price * (-1).toBigDecimal()
            val request = createRequest(
                price = minusPrice
            )

            // when
            val exception = shouldThrowAny {
                service.changePrice(
                    menuId,
                    request
                )
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 메뉴 가격이 메뉴 상품들의 가격 합보다 큰 경우") {
            // given
            val menuId = savedMenu.id
            val overPrice = savedMenu.price * (2).toBigDecimal()
            val request = createRequest(
                price = overPrice
            )

            // when
            val exception = shouldThrowAny {
                service.changePrice(
                    menuId,
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
            price: BigDecimal = 1000.toBigDecimal()
        ): Menu {
            return Menu().apply {
                this.price = price
            }
        }
    }
}
