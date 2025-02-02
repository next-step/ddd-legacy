package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.transaction.Transactional
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.util.*


@SpringBootTest
@Transactional
class MenuServiceTest : BehaviorSpec() {
    @Autowired
    private lateinit var menuService: MenuService

    @Autowired
    private lateinit var menuGroupRepository: MenuGroupRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var menuRepository: MenuRepository

    init {
        Given("메뉴를 생성할 때") {
            val menuGroup = MenuGroup().apply {
                id = UUID.randomUUID()
                name = "메뉴 그룹"
            }
            menuGroupRepository.save(menuGroup)

            val product1 = Product().apply {
                id = UUID.randomUUID()
                name = "상품1"
                price = BigDecimal("1000")
            }
            val product2 = Product().apply {
                id = UUID.randomUUID()
                name = "상품2"
                price = BigDecimal("2000")
            }
            productRepository.saveAll(listOf(product1, product2))

            When("메뉴명에 욕설이 포함된 경우") {
                val menu = Menu().apply {
                    name = "인생의 시발점이 될 메뉴"
                    price = BigDecimal("1000")
                    menuGroupId = menuGroup.id
                    menuProducts = listOf()
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        menuService.create(menu)
                    }
                }
            }

            When("올바른 메뉴 정보가 주어지면") {
                val menuProducts = listOf(
                    MenuProduct().apply {
                        productId = product1.id
                        quantity = 1
                    },
                    MenuProduct().apply {
                        productId = product2.id
                        quantity = 1
                    }
                )

                val menu = Menu().apply {
                    name = "메뉴"
                    price = BigDecimal("3000")
                    menuGroupId = menuGroup.id
                    this.menuProducts = menuProducts
                }

                Then("메뉴가 정상적으로 생성된다") {
                    val savedMenu = menuService.create(menu)

                    savedMenu.id shouldNotBe null
                    savedMenu.name shouldBe "메뉴"
                    savedMenu.price shouldBe BigDecimal("3000")
                    savedMenu.menuProducts shouldHaveSize 2
                }
            }

            When("가격이 음수인 경우") {
                val menu = Menu().apply {
                    name = "메뉴"
                    price = BigDecimal("-1000")
                    menuGroupId = menuGroup.id
                    menuProducts = listOf()
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        menuService.create(menu)
                    }
                }
            }

            When("메뉴 상품 목록이 비어있는 경우") {
                val menu = Menu().apply {
                    name = "메뉴"
                    price = BigDecimal("1000")
                    menuGroupId = menuGroup.id
                    menuProducts = listOf()
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        menuService.create(menu)
                    }
                }
            }

            When("메뉴 가격이 구성하는 상품의 가격 합보다 큰 경우") {
                val menuProducts = listOf(
                    MenuProduct().apply {
                        productId = product1.id
                        quantity = 1
                    }
                )

                val menu = Menu().apply {
                    name = "메뉴"
                    price = BigDecimal("2000")
                    menuGroupId = menuGroup.id
                    this.menuProducts = menuProducts
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        menuService.create(menu)
                    }
                }
            }
        }

        Given("메뉴 가격을 변경할 때") {
            val menuGroup = MenuGroup().apply {
                id = UUID.randomUUID()
                name = "메뉴 그룹"
            }
            menuGroupRepository.save(menuGroup)

            val product = Product().apply {
                id = UUID.randomUUID()
                name = "상품"
                price = BigDecimal("1000")
            }
            productRepository.save(product)

            val menuProduct = MenuProduct().apply {
                this.product = product
                quantity = 1
            }

            val menu = Menu().apply {
                id = UUID.randomUUID()
                name = "메뉴"
                price = BigDecimal("1000")
                this.menuGroup = menuGroup
                menuProducts = listOf(menuProduct)
            }
            menuRepository.save(menu)

            When("새로운 가격이 구성하는 상품의 가격 합 이하인 경우") {
                Then("가격이 정상적으로 변경된다") {
                    val updatedMenu = menuService.changePrice(menu.id!!, Menu().apply {
                        price = BigDecimal("900")
                    })

                    updatedMenu.price shouldBe BigDecimal("900")
                }
            }

            When("새로운 가격이 구성하는 상품의 가격 합보다 큰 경우") {
                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        menuService.changePrice(menu.id!!, Menu().apply {
                            price = BigDecimal("1100")
                        })
                    }
                }
            }
        }

        Given("메뉴를 표시/숨김 처리할 때") {
            val menuGroup = MenuGroup().apply {
                id = UUID.randomUUID()
                name = "메뉴 그룹"
            }
            menuGroupRepository.save(menuGroup)

            val product = Product().apply {
                id = UUID.randomUUID()
                name = "상품"
                price = BigDecimal("1000")
            }
            productRepository.save(product)

            val menuProduct = MenuProduct().apply {
                this.product = product
                quantity = 1
            }

            val menu = Menu().apply {
                id = UUID.randomUUID()
                name = "메뉴"
                price = BigDecimal("1000")
                this.menuGroup = menuGroup
                menuProducts = listOf(menuProduct)
                isDisplayed = false
            }
            menuRepository.save(menu)

            When("메뉴를 표시할 때") {
                Then("displayed가 true로 변경된다") {
                    val displayedMenu = menuService.display(menu.id!!)
                    displayedMenu.isDisplayed shouldBe true
                }
            }

            When("메뉴를 숨길 때") {
                Then("displayed가 false로 변경된다") {
                    val hiddenMenu = menuService.hide(menu.id!!)
                    hiddenMenu.isDisplayed shouldBe false
                }
            }
        }
    }
}
