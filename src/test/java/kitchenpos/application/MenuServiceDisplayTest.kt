package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.mockk
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuRepository
import kitchenpos.testsupport.FakeMenuGroupRepository
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeProductRepository
import kitchenpos.testsupport.MenuFixtures
import kitchenpos.testsupport.ProductFixtures

class MenuServiceDisplayTest : ShouldSpec({
    lateinit var menuRepository: MenuRepository
    lateinit var service: MenuService

    lateinit var savedMenu: Menu

    beforeTest {
        menuRepository = FakeMenuRepository()

        savedMenu = menuRepository.save(
            MenuFixtures.createMenu(
                product = ProductFixtures.createProduct()
            )
        )

        service = MenuService(
            menuRepository,
            FakeMenuGroupRepository(),
            FakeProductRepository(),
            mockk()
        )
    }

    context("메뉴 노출 처리") {
        should("성공") {
            // given
            val menuId = savedMenu.id

            // when
            val changedMenu = service.display(menuId)

            // then
            changedMenu.id shouldBe menuId
            changedMenu.isDisplayed shouldBe true
        }

        should("실패 - 메뉴 가격이 메뉴 상품들의 가격 합보다 큰 경우") {
            // given
            val product = ProductFixtures.createProduct()
            savedMenu = menuRepository.save(
                MenuFixtures.createMenu(
                    product = product,
                    price = product.price * (2).toBigDecimal()
                )
            )
            val menuId = savedMenu.id

            // when
            val exception = shouldThrowAny {
                service.display(menuId)
            }

            // then
            exception.shouldBeTypeOf<IllegalStateException>()
        }
    }
})
