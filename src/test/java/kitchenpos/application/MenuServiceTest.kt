package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import kitchenpos.domain.FakeMenuGroupRepository
import kitchenpos.domain.FakeMenuRepository
import kitchenpos.domain.FakeProductRepository
import kitchenpos.domain.Menu
import kitchenpos.infra.PurgomalumClient
import java.math.BigDecimal

private val menuRepository = FakeMenuRepository()
private val menuGroupRepository = FakeMenuGroupRepository()
private val productRepository = FakeProductRepository()
private val purgomalumClient = mockk<PurgomalumClient>()

private val menuService = MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient)

private fun Menu.persistAll() = this.persistMenuGroup().persistProducts().also { menuRepository.save(this) }

private fun Menu.persistMenuGroup() = this.also { menuGroupRepository.save(this.menuGroup) }

private fun Menu.persistProducts() = this.also { this.menuProducts.forEach { productRepository.save(it.product) } }

private val Int.won: BigDecimal get() = this.toBigDecimal()
private val Int.pcs: Long get() = this.toLong()

class MenuServiceTest : BehaviorSpec({
    given("메뉴를 생성할 때") {
        `when`("메뉴 가격이 null이면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = null
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.create(newMenu)
                }
            }
        }

        `when`("메뉴의 가격이 0보다 작으면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = (-1).won
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.create(newMenu)
                }
            }
        }

        `when`("메뉴 그룹이 존재하지 않으면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                }

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    menuService.create(newMenu)
                }
            }
        }

        `when`("선택된 메뉴가 존재하지 않으면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }.persistMenuGroup()

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.create(newMenu)
                }
            }
        }

        `when`("메뉴 이름이 null이면") {
            val newMenu =
                buildMenu {
                    name = null
                    price = 1000.won
                    groupName("추천 메뉴")
                    products {
                        item("치킨1", 16000.won, 1.pcs)
                    }
                }.persistMenuGroup().persistProducts()

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
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }.persistAll()

            then("메뉴의 가격이 변경된다.") {
                menuService.changePrice(newMenu.id, newMenu)

                with(menuRepository.findById(newMenu.id)?.get()) {
                    this?.price = BigDecimal(1000)
                }
            }
        }

        `when`("메뉴 가격이 null이면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = null
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.changePrice(newMenu.id, newMenu)
                }
            }
        }

        `when`("메뉴의 가격이 0보다 작으면") {
            val changedMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }.persistAll().apply { price = (-1).won }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.changePrice(changedMenu.id, changedMenu)
                }
            }
        }

        `when`("메뉴가 존재하지 않으면") {
            val changedMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    menuService.changePrice(changedMenu.id, changedMenu)
                }
            }
        }

        `when`("메뉴의 가격이 상품의 총 합보다 크면") {
            val changedMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }.persistAll().apply {
                    price = 16001.toBigDecimal()
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    menuService.changePrice(changedMenu.id, changedMenu)
                }
            }
        }
    }

    given("메뉴를 노출할 때") {
        `when`("입력 값이 정상이면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }.persistAll()

            then("메뉴가 노출된다.") {
                with(menuService.display(newMenu.id)) {
                    this.isDisplayed = true
                }
            }
        }

        `when`("메뉴가 존재하지 않으면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    menuService.display(newMenu.id)
                }
            }
        }

        `when`("메뉴의 가격이 상품의 총 합보다 크면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }.persistAll().apply {
                    price = 16001.toBigDecimal()
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    menuService.display(newMenu.id)
                }
            }
        }
    }

    given("메뉴를 숨길 때") {
        `when`("입력 값이 정상이면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }.persistAll()

            then("메뉴가 숨겨진다.") {
                with(menuService.hide(newMenu.id)) {
                    this.isDisplayed = false
                }
            }
        }

        `when`("메뉴가 존재하지 않으면") {
            val newMenu =
                buildMenu {
                    name = "메뉴"
                    price = 1000.won
                    groupName("추천 메뉴")
                    products { item("치킨1", 16000.won, 1.pcs) }
                }

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    menuService.hide(newMenu.id)
                }
            }
        }
    }
})
