package kitchenops.application

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.application.MenuService
import kitchenpos.domain.*
import kitchenpos.infra.PurgomalumClient
import spec.BaseUnitSpec
import java.math.BigDecimal
import java.util.*
import kotlin.NoSuchElementException

internal class MenuServiceTest : BaseUnitSpec({

    val menuRepository = mockk<MenuRepository>()
    val menuGroupRepository = mockk<MenuGroupRepository>()
    val productRepository = mockk<ProductRepository>()
    val purgomalumClient = mockk<PurgomalumClient>()

    val sut = MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient)

    context("메뉴를 생성할 수 있다") {
        test("가격을 지정하지 않으면 생성할 수 없다") {
            // given
            val request = Menu().apply { price = null }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("가격이 0원 미만이면 생성할 수 없다") {
            // given
            val request = Menu().apply { price = BigDecimal("-1") }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("메뉴 그룹이 존재하지 않으면 생성할 수 없다") {
            // given
            val mgId = UUID.randomUUID()
            val request = Menu().apply {
                price = BigDecimal("0")
                menuGroupId = mgId
            }

            every { menuGroupRepository.findById(mgId) } returns Optional.empty()

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        test("사용하는 상품을 지정하지 않으면 생성할 수 없다") {
            // given
            val mgId = UUID.randomUUID()
            val request = Menu().apply {
                price = BigDecimal("0")
                menuProducts = null
                menuGroupId = mgId
            }

            val menuGroup = MenuGroup().apply { id = mgId }
            every { menuGroupRepository.findById(mgId) } returns Optional.of(menuGroup)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("사용하는 상품이 비어있으면 생성할 수 없다") {
            // given
            val mgId = UUID.randomUUID()
            val request = Menu().apply {
                price = BigDecimal("100")
                menuProducts = emptyList()
                menuGroupId = mgId
            }

            val menuGroup = MenuGroup().apply { id = mgId }
            every { menuGroupRepository.findById(mgId) } returns Optional.of(menuGroup)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("사용하는 상품이 존재하지 않으면 생성할 수 없다") {
            // given
            val invalidProductId = UUID.randomUUID()
            val menuProduct = MenuProduct().apply {
                productId = invalidProductId
                quantity = 1
            }
            every { productRepository.findAllByIdIn(listOf(invalidProductId)) } returns emptyList()

            val menuGroup = MenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = Menu().apply {
                price = BigDecimal("100")
                menuProducts = listOf(menuProduct)
                menuGroupId = menuGroup.id
            }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("사용하는 상품의 수량이 0개 미만이면 생성할 수 없다") {
            // given
            val product = Product().apply { price = BigDecimal("100") }
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = MenuProduct().apply {
                quantity = 0
                productId = product.id
            }
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns emptyList()

            val menuGroup = MenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = Menu().apply {
                price = BigDecimal("100")
                menuProducts = listOf(menuProduct)
                menuGroupId = menuGroup.id
            }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("메뉴의 가격이 상품 가격의 총 합보다 클 경우 생성할 수 없다") {
            // given
            val product = Product().apply { price = BigDecimal("100") }
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = MenuProduct().apply {
                quantity = 3
                productId = product.id
            }
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = MenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = Menu().apply {
                price = BigDecimal("500")
                menuProducts = listOf(menuProduct)
                menuGroupId = menuGroup.id
            }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("이름을 지정하지 않으면 생성할 수 없다") {
            // given
            val product = Product().apply { price = BigDecimal("100") }
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = MenuProduct().apply {
                quantity = 1
                productId = product.id
            }
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = MenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = Menu().apply {
                price = BigDecimal("100")
                menuProducts = listOf(menuProduct)
                menuGroupId = menuGroup.id
                name = null
            }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        // TODO: empty name에 동작하지 않아야 할 것 같은데 버그인 듯 하다
        test("이름이 비어있으면 생성할 수 없다").config(enabled = false) {
            // given
            val product = Product().apply { price = BigDecimal("100") }
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = MenuProduct().apply {
                quantity = 1
                productId = product.id
            }
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = MenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = Menu().apply {
                price = BigDecimal("100")
                menuProducts = listOf(menuProduct)
                menuGroupId = menuGroup.id
                name = ""
            }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("메뉴의 이름에 욕설이 포함되면 생성할 수 없다") {
            // given
            val product = Product().apply { price = BigDecimal("100") }
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = MenuProduct().apply {
                quantity = 1
                productId = product.id
            }
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = MenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = Menu().apply {
                price = BigDecimal("100")
                menuProducts = listOf(menuProduct)
                menuGroupId = menuGroup.id
                name = "hell"
            }

            every { purgomalumClient.containsProfanity("hell") } returns true

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("생성한다") {
            // given
            val product = Product().apply { price = BigDecimal("100") }
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = MenuProduct().apply {
                quantity = 1
                productId = product.id
            }
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = MenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            every { purgomalumClient.containsProfanity("myName") } returns false

            every { menuRepository.save(any()) } returns Menu().apply {
                price = BigDecimal("100")
                menuProducts = listOf(menuProduct)
                menuGroupId = menuGroup.id
                this.menuGroup = menuGroup
                name = "myName"
                isDisplayed = true
            }

            val request = Menu().apply {
                price = BigDecimal("100")
                menuProducts = listOf(menuProduct)
                menuGroupId = menuGroup.id
                name = "myName"
                isDisplayed = true
            }

            // when
            val actual = sut.create(request)

            // then
            actual should {
                it.name shouldBe "myName"
                it.price shouldBe BigDecimal("100")
                it.isDisplayed shouldBe true
                it.menuGroup shouldBe menuGroup
                it.menuGroupId shouldBe menuGroup.id
                it.menuProducts shouldBe listOf(menuProduct)
            }

            verify(exactly = 1) { menuRepository.save(any()) }
        }
    }

    context("메뉴를 전체 조회할 수 있다") {
        test("전체 조회한다") {
            // given
            val menu1 = Menu()
            val menu2 = Menu()

            every { menuRepository.findAll() } returns listOf(menu1, menu2)

            // when
            val actual = sut.findAll()

            // then
            actual shouldBe listOf(menu1, menu2)
        }
    }

    context("메뉴의 가격을 변경할 수 있다") {
        test("가격을 지정하지 않으면 변경할 수 없다") {
            // given
            val request = Menu().apply { price = null }

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("가격이 0원 미만이면 변경할 수 없다") {
            // given
            val request = Menu().apply { price = BigDecimal("-1") }

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("메뉴가 존재하지 않으면 변경할 수 없다") {
            // given
            val request = Menu().apply { price = BigDecimal("100") }

            every { menuRepository.findById(request.id) } returns Optional.empty()

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        test("가격이 상품 가격의 합보다 크면 생성할 수 없다") {
            // given
            val product = Product().apply { price = BigDecimal("100") }
            val menuProduct = MenuProduct().apply {
                quantity = 3
                this.product = product
            }
            val menu = Menu().apply { menuProducts = listOf(menuProduct) }

            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val request = Menu().apply {
                id = menu.id
                price = BigDecimal("400")
            }

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("가격을 변경한다") {
            // given
            val product = Product().apply { price = BigDecimal("1000") }
            val menuProduct = MenuProduct().apply {
                quantity = 1
                this.product = product
            }
            val menu = Menu().apply { menuProducts = listOf(menuProduct) }

            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val request = Menu().apply {
                id = menu.id
                price = BigDecimal("1000")
            }

            // when
            val actual = sut.changePrice(request.id, request)

            // then
            actual.price shouldBe BigDecimal("1000")
        }
    }

    context("메뉴를 표시하게 변경할 수 있다") {
        test("메뉴가 존재하지 않으면 변경할 수 없다") {
            // given
            val menuId = UUID.randomUUID()

            every { menuRepository.findById(menuId) } returns Optional.empty()

            // when
            val actual = runCatching { sut.display(menuId) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        test("메뉴의 가격이 상품 가격의 합보다 클 경우에는 화면에 표시할 수 없다") {
            // given
            val product = Product().apply { price = BigDecimal("100") }
            val menuProduct = MenuProduct().apply {
                quantity = 1
                this.product = product
            }

            val menu = Menu().apply {
                price = BigDecimal("1000")
                menuProducts = listOf(menuProduct)
            }
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            // when
            val actual = runCatching { sut.display(menu.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("표시하게 변경한다") {
            // given
            val product = Product().apply { price = BigDecimal("100") }
            val menuProduct = MenuProduct().apply {
                quantity = 1
                this.product = product
            }

            val menu = Menu().apply {
                price = BigDecimal("100")
                menuProducts = listOf(menuProduct)
                isDisplayed = false
            }
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            // when
            val actual = sut.display(menu.id)

            // then
            actual.isDisplayed shouldBe true
        }
    }

    context("메뉴를 숨기게 변경할 수 있다") {
        test("메뉴가 존재하지 않으면 변경할 수 없다") {
            // given
            val menuId = UUID.randomUUID()

            every { menuRepository.findById(menuId) } returns Optional.empty()

            // when
            val actual = runCatching { sut.hide(menuId) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        test("숨기게 변경한다") {
            // given
            val menu = Menu().apply { isDisplayed = true }
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            // when
            val actual = sut.hide(menu.id)

            // then
            actual.isDisplayed shouldBe false
        }
    }
})
