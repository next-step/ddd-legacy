package kitchenpos.application

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*
import kotlin.NoSuchElementException

class MenuServiceTest : DescribeSpec() {
    init {
        describe("MenuService 클래스의") {
            val menuRepository = mockk<MenuRepository>()
            val menuGroupRepository = mockk<MenuGroupRepository>()
            val productRepository = mockk<ProductRepository>()
            val purgomalumClient = mockk<PurgomalumClient>()

            val menuService =
                MenuService(
                    menuRepository,
                    menuGroupRepository,
                    productRepository,
                    purgomalumClient,
                )

            describe("create 메서드는") {
                context("요청에 가격이 존재하지 않을 때") {
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴"
                            this.menuGroupId = UUID.randomUUID()
                        }
                    it("IllegalArgumentException을 던진다") {
                        every { menuGroupRepository.findById(any()) } returns Optional.ofNullable(null)

                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴 그룹이 존재하지 않을 때") {
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴"
                            this.menuGroupId = UUID.randomUUID()
                            this.price = BigDecimal("5000")
                        }
                    it("NoSuchElementException을 던진다") {
                        every { menuGroupRepository.findById(any()) } returns Optional.ofNullable(null)

                        assertThrows<NoSuchElementException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴 상품이 존재하지 않을 때") {
                    val menuProduct =
                        MenuProduct().apply {
                            this.productId = UUID.randomUUID()
                            this.quantity = 1
                        }
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴"
                            this.menuGroupId = UUID.randomUUID()
                            this.menuProducts = listOf(menuProduct)
                        }
                    it("IllegalArgumentException을 던진다") {
                        every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
                        every { productRepository.findAllByIdIn(any()) } returns emptyList()

                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴 상품의 수량이 음수일 때") {
                    val menuProduct =
                        MenuProduct().apply {
                            this.productId = UUID.randomUUID()
                            this.quantity = -1
                        }
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴"
                            this.menuGroupId = UUID.randomUUID()
                            this.menuProducts = listOf(menuProduct)
                        }
                    it("IllegalArgumentException을 던진다") {
                        every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
                        every { productRepository.findAllByIdIn(any()) } returns listOf(Product())

                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴의 가격이 메뉴 상품의 총 가격보다 클 때") {
                    val menuProduct =
                        MenuProduct().apply {
                            this.productId = UUID.randomUUID()
                            this.quantity = 1
                        }
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴"
                            this.price = BigDecimal("5000")
                            this.menuGroupId = UUID.randomUUID()
                            this.menuProducts = listOf(menuProduct)
                        }
                    it("IllegalArgumentException을 던진다") {
                        every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
                        every { productRepository.findById(any()) } returns
                            Optional.of(
                                Product().apply {
                                    this.price = BigDecimal("1000")
                                },
                            )

                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }

                context("메뉴의 이름에 비속어가 포함되어 있을 때") {
                    val createMenuRequest =
                        Menu().apply {
                            this.id = UUID.randomUUID()
                            this.name = "삐이이"
                            this.menuGroupId = UUID.randomUUID()
                        }
                    it("IllegalArgumentException을 던진다") {
                        every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
                        every { purgomalumClient.containsProfanity(any()) } returns true

                        assertThrows<IllegalArgumentException> {
                            menuService.create(createMenuRequest)
                        }
                    }
                }
            }
        }
    }
}
