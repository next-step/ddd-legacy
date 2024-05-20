package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.util.UUID
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import kitchenpos.testsupport.FakeMenuGroupRepository
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeProductRepository
import kitchenpos.testsupport.MenuGroupFixtures.createMenuGroup
import kitchenpos.testsupport.ProductFixtures.createProduct

class MenuServiceCreateTest : ShouldSpec({
    lateinit var menuRepository: MenuRepository
    lateinit var menuGroupRepository: MenuGroupRepository
    lateinit var productRepository: ProductRepository
    lateinit var purgomalumClient: PurgomalumClient
    lateinit var service: MenuService

    lateinit var savedMenuGroup: MenuGroup
    lateinit var savedProduct: Product

    beforeTest {
        menuRepository = FakeMenuRepository()
        menuGroupRepository = FakeMenuGroupRepository()
        productRepository = FakeProductRepository()
        purgomalumClient = mockk {
            every { containsProfanity(any()) } returns false
        }

        savedMenuGroup = menuGroupRepository.save(
            createMenuGroup()
        )

        savedProduct = productRepository.save(
            createProduct()
        )

        service = MenuService(
            menuRepository,
            menuGroupRepository,
            productRepository,
            purgomalumClient,
        )
    }

    context("메뉴 생성") {
        should("성공") {
            // given
            val request = createRequest(
                price = savedProduct.price,
                menuGroup = savedMenuGroup,
                product = savedProduct
            )

            // when
            val menu = service.create(request)

            // then
            menu.price shouldBe request.price
            menu.isDisplayed shouldBe true
        }

        should("실패 - 가격이 0원 미만인 경우") {
            // given
            val request = createRequest(
                price = (-1000).toBigDecimal(),
                menuGroup = savedMenuGroup,
                product = savedProduct
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 메뉴에 상품이 등록되지 않은 경우") {
            // given
            val request = createRequest(
                price = (-1000).toBigDecimal(),
                menuGroup = savedMenuGroup,
                menuProducts = emptyList()
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 메뉴명이 비어있는 경우") {
            // given
            val request = createRequest(
                price = savedProduct.price,
                menuGroup = savedMenuGroup,
                product = savedProduct,
                name = null
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 메뉴명에 욕설이 포함된 경우") {
            // given
            val request = createRequest(
                price = savedProduct.price,
                menuGroup = savedMenuGroup,
                product = savedProduct
            )
            every {
                purgomalumClient.containsProfanity(any())
            } returns true

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }
    }
}) {
    companion object {
        private fun createRequest(
            price: BigDecimal = 1000.toBigDecimal(),
            name: String? = "test-menu-name",
            isDisplayed: Boolean = true,
            menuGroup: MenuGroup,
            product: Product? = null,
            menuProducts: List<MenuProduct> = listOf(
                MenuProduct().apply {
                    this.product = product
                    this.productId = product?.id
                    this.quantity = 1
                }
            )
        ): Menu {
            return Menu().apply {
                this.id = UUID.randomUUID()
                this.menuGroupId = menuGroup.id
                this.menuGroup = menuGroup
                this.price = price
                this.name = name
                this.isDisplayed = isDisplayed
                this.menuProducts = menuProducts
            }
        }
    }
}
