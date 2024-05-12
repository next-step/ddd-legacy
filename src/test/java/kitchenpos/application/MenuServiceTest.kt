package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import kitchenpos.domain.FakeMenuGroupRepository
import kitchenpos.domain.FakeMenuRepository
import kitchenpos.domain.FakeProductRepository
import kitchenpos.dsl.groupName
import kitchenpos.dsl.menu
import kitchenpos.dsl.price
import kitchenpos.dsl.products
import kitchenpos.infra.PurgomalumClient
import java.math.BigDecimal

private val menuRepository = FakeMenuRepository()
private val menuGroupRepository = FakeMenuGroupRepository()
private val productRepository = FakeProductRepository()
private val purgomalumClient = mockk<PurgomalumClient>()

private val menuService = MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient)

private fun createMenu() =
    menu("치킨 커플 셋트", 16000) {
        groupName("추천 메뉴")
        products {
            "치킨1" price 16000 seq 1
            "치킨1" price 16000 seq 1
        }
    }.also {
        menuGroupRepository.save(it.menuGroup)
        it.menuProducts.forEach { productRepository.save(it.product) }
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
