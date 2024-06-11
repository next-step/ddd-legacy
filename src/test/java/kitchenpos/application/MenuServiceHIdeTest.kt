package kitchenpos.application

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuRepository
import kitchenpos.testsupport.FakeMenuGroupRepository
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeProductRepository
import kitchenpos.testsupport.MenuFixtures
import kitchenpos.testsupport.ProductFixtures

class MenuServiceHIdeTest : ShouldSpec({
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

    context("메뉴 숨김 처리") {
        should("성공") {
            // given
            val menuId = savedMenu.id

            // when
            val changedMenu = service.hide(menuId)

            // then
            changedMenu.id shouldBe menuId
            changedMenu.isDisplayed shouldBe false
        }
    }
})
