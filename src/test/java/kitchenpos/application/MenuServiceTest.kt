package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import kitchenpos.domain.FakeMenuGroupRepository
import kitchenpos.domain.FakeMenuRepository
import kitchenpos.domain.FakeProductRepository
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product
import kitchenpos.infra.PurgomalumClient
import java.math.BigDecimal
import java.util.UUID

private val menuRepository = FakeMenuRepository()
private val menuGroupRepository = FakeMenuGroupRepository()
private val productRepository = FakeProductRepository()
private val purgomalumClient = mockk<PurgomalumClient>()

private val menuService = MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient)

private fun createMenu() =
    Menu()
        .apply {
            name = "후라이드 치킨"
            price = BigDecimal(16000)
            menuGroupId = UUID.randomUUID()
            menuProducts = listOf(MenuProduct())
        }
        .also {
            menuGroupRepository.save(
                MenuGroup().apply {
                    id = it.menuGroupId
                    name = "치킨"
                }
            )
            productRepository.save(
                Product().apply {
                    id = it.menuProducts[0].productId
                    name = "후라이드 치킨"
                    price = BigDecimal(16000)
                }
            )
        }

class MenuServiceTest : BehaviorSpec({
    given("메뉴를 생성할 때") {
        `when`("메뉴 가격이 null이면") {
            val newMenu = createMenu().apply { price = null }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.create(newMenu)
                }
            }
        }

        `when`("메뉴의 가격이 0보다 작으면") {
            val newMenu = createMenu().apply { price = BigDecimal(-1) }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.create(newMenu)
                }
            }
        }

        `when`("메뉴 그룹이 존재하지 않으면") {
            val newMenu = createMenu().also { menuGroupRepository.clear() }

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    menuService.create(newMenu)
                }
            }
        }

        `when`("선택된 메뉴가 존재하지 않으면") {
            val newMenu = createMenu().also { productRepository.clear() }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.create(newMenu)
                }
            }
        }

        `when`("메뉴 이름이 null이면") {
            val newMenu = createMenu().apply { name = null }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.create(newMenu)
                }
            }
        }
    }
})
