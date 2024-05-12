package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import kitchenpos.domain.FakeMenuGroupRepository
import kitchenpos.domain.FakeMenuRepository
import kitchenpos.domain.FakeProductRepository
import kitchenpos.dsl.groupName
import kitchenpos.dsl.item
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

private fun createMenu(
    name: String? = "치킨 커플 세트",
    price: Int? = 10000
) = menu(name, price) {
    groupName("추천 메뉴")
    products {
        item("치킨1" price 16000 quantity 1)
        item("치킨1" price 16000 quantity 1)
    }
}.also {
    menuGroupRepository.save(it.menuGroup)
    it.menuProducts.forEach { productRepository.save(it.product) }
}

class MenuServiceTest : BehaviorSpec({
    given("메뉴를 생성할 때") {
        `when`("메뉴 가격이 null이면") {
            val newMenu = createMenu(price = null)

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.create(newMenu)
                }
            }
        }

        `when`("메뉴의 가격이 0보다 작으면") {
            val newMenu = createMenu(price = -1)

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
            val newMenu = createMenu(name = null)

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.create(newMenu)
                }
            }
        }
    }

    given("메뉴 가격을 변경할 때") {
        `when`("입력 값이 정상이면") {
            val newMenu =
                createMenu()
                    .also { menuRepository.save(it) }
                    .apply {
                        price = BigDecimal(20)
                    }

            then("메뉴의 가격이 변경된다.") {
                menuService.changePrice(newMenu.id, newMenu)

                with(menuRepository.findById(newMenu.id)?.get()) {
                    this?.price = BigDecimal(20000)
                }
            }
        }

        `when`("메뉴 가격이 null이면") {
            val newMenu = createMenu(price = null)

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.changePrice(newMenu.id, newMenu)
                }
            }
        }

        `when`("메뉴의 가격이 0보다 작으면") {
            val newMenu = createMenu(price = -1)

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.changePrice(newMenu.id, newMenu)
                }
            }
        }

        `when`("메뉴가 존재하지 않으면") {
            val newMenu = createMenu()

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    menuService.changePrice(newMenu.id, newMenu)
                }
            }
        }

        `when`("메뉴의 가격이 상품의 총 합보다 크면") {
            val newMenu =
                createMenu().also { menuRepository.save(it) }.apply {
                    price = BigDecimal(100000)
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.changePrice(newMenu.id, newMenu)
                }
            }
        }
    }

    given("메뉴를 노출할 때") {
        `when`("입력 값이 정상이면") {
            val newMenu = createMenu().also { menuRepository.save(it) }

            then("메뉴가 노출된다.") {
                with(menuService.display(newMenu.id)) {
                    this.isDisplayed = true
                }
            }
        }

        `when`("메뉴가 존재하지 않으면") {
            val newMenu = createMenu()

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    menuService.display(newMenu.id)
                }
            }
        }

        `when`("메뉴의 가격이 상품의 총 합보다 크면") {
            val newMenu =
                createMenu(price = Int.MAX_VALUE).also { menuRepository.save(it) }

            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    menuService.display(newMenu.id)
                }
            }
        }
    }

    given("메뉴를 숨길 때") {
        `when`("입력 값이 정상이면") {
            val newMenu = createMenu().also { menuRepository.save(it) }

            then("메뉴가 숨겨진다.") {
                with(menuService.hide(newMenu.id)) {
                    this.isDisplayed = false
                }
            }
        }

        `when`("메뉴가 존재하지 않으면") {
            val newMenu = createMenu()

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    menuService.hide(newMenu.id)
                }
            }
        }
    }
})
