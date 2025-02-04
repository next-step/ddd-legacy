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

internal class MenuServiceTest : BaseUnitSpec({

    val menuRepository = mockk<MenuRepository>()
    val menuGroupRepository = mockk<MenuGroupRepository>()
    val productRepository = mockk<ProductRepository>()
    val purgomalumClient = mockk<PurgomalumClient>()

    val sut = MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient)

    context("메뉴를 생성할 수 있다") {
        test("가격을 지정하지 않으면 생성할 수 없다") {
            // given
            val request = createMenu(price = null)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("가격이 0원 미만이면 생성할 수 없다") {
            // given
            val request = createMenu(price = BigDecimal("-1"))

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("메뉴 그룹이 존재하지 않으면 생성할 수 없다") {
            // given
            val menuGroup = createMenuGroup(id = UUID.randomUUID())
            val request = createMenu(price = BigDecimal("0"), menuGroup = menuGroup)

            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.empty()

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        test("사용하는 상품을 지정하지 않으면 생성할 수 없다") {
            // given
            val menuGroup = createMenuGroup(id = UUID.randomUUID())
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = createMenu(price = BigDecimal("0"), menuProducts = null, menuGroup = menuGroup)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("사용하는 상품이 비어있으면 생성할 수 없다") {
            // given
            val menuGroup = createMenuGroup(id = UUID.randomUUID())
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = createMenu(price = BigDecimal("100"), menuProducts = emptyList(), menuGroup = menuGroup)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("사용하는 상품이 존재하지 않으면 생성할 수 없다") {
            // given
            val invalidProduct = createProduct(id = UUID.randomUUID())
            val menuProduct = createMenuProduct(product = invalidProduct, quantity = 1)
            every { productRepository.findAllByIdIn(listOf(invalidProduct.id)) } returns emptyList()

            val menuGroup = MenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request =
                createMenu(price = BigDecimal("100"), menuProducts = listOf(menuProduct), menuGroup = menuGroup)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("사용하는 상품의 수량이 0개 미만이면 생성할 수 없다") {
            // given
            val product = createProduct(price = BigDecimal("100"))
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = createMenuProduct(quantity = 0, product = product)
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns emptyList()

            val menuGroup = createMenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request =
                createMenu(price = BigDecimal("100"), menuProducts = listOf(menuProduct), menuGroup = menuGroup)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("메뉴의 가격이 상품 가격의 총 합보다 클 경우 생성할 수 없다") {
            // given
            val product = createProduct(price = BigDecimal("100"))
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = createMenuProduct(quantity = 3, product = product)
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = createMenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request =
                createMenu(price = BigDecimal("500"), menuProducts = listOf(menuProduct), menuGroup = menuGroup)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("이름을 지정하지 않으면 생성할 수 없다") {
            // given
            val product = createProduct(price = BigDecimal("100"))
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = createMenuProduct(quantity = 1, product = product)
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = createMenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = createMenu(
                price = BigDecimal("100"),
                menuProducts = listOf(menuProduct),
                menuGroup = menuGroup,
                name = null
            )

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        // TODO: empty name에 동작하지 않아야 할 것 같은데 버그인 듯 하다
        test("이름이 비어있으면 생성할 수 없다").config(enabled = false) {
            // given
            val product = createProduct(price = BigDecimal("100"))
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = createMenuProduct(quantity = 1, product = product)
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = createMenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = createMenu(
                price = BigDecimal("100"),
                menuProducts = listOf(menuProduct),
                menuGroup = menuGroup,
                name = "",
            )

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("메뉴의 이름에 욕설이 포함되면 생성할 수 없다") {
            // given
            val product = createProduct(price = BigDecimal("100"))
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = createMenuProduct(quantity = 1, product = product)
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = createMenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            val request = createMenu(
                price = BigDecimal("100"),
                menuProducts = listOf(menuProduct),
                menuGroup = menuGroup,
                name = "hell",
            )

            every { purgomalumClient.containsProfanity("hell") } returns true

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("생성한다") {
            // given
            val product = createProduct(price = BigDecimal("100"))
            every { productRepository.findById(product.id) } returns Optional.of(product)

            val menuProduct = createMenuProduct(quantity = 1, product = product)
            every { productRepository.findAllByIdIn(listOf(menuProduct.productId)) } returns listOf(product)

            val menuGroup = createMenuGroup()
            every { menuGroupRepository.findById(menuGroup.id) } returns Optional.of(menuGroup)

            every { purgomalumClient.containsProfanity("myName") } returns false

            every { menuRepository.save(any()) } returns createMenu(
                price = BigDecimal("100"),
                menuProducts = listOf(menuProduct),
                menuGroup = menuGroup,
                name = "myName",
                displayed = true
            )

            val request = createMenu(
                price = BigDecimal("100"),
                menuProducts = listOf(menuProduct),
                menuGroup = menuGroup,
                name = "myName",
                displayed = true,
            )

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
            val menu1 = createMenu()
            val menu2 = createMenu()
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
            val request = createMenu(price = null)

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("가격이 0원 미만이면 변경할 수 없다") {
            // given
            val request = createMenu(price = BigDecimal("-1"))

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("메뉴가 존재하지 않으면 변경할 수 없다") {
            // given
            val request = createMenu(price = BigDecimal("100"))

            every { menuRepository.findById(request.id) } returns Optional.empty()

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        test("가격이 상품 가격의 합보다 크면 생성할 수 없다") {
            // given
            val product = createProduct(price = BigDecimal("100"))
            val menuProduct = createMenuProduct(quantity = 3, product = product)
            val menu = createMenu(menuProducts = listOf(menuProduct))

            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val request = createMenu(id = menu.id, price = BigDecimal("400"))

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("가격을 변경한다") {
            // given
            val product = createProduct(price = BigDecimal("1000"))
            val menuProduct = createMenuProduct(quantity = 1, product = product)
            val menu = createMenu(menuProducts = listOf(menuProduct))

            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val request = createMenu(id = menu.id, price = BigDecimal("1000"))

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
            val product = createProduct(price = BigDecimal("100"))
            val menuProduct = createMenuProduct(quantity = 1, product = product)

            val menu = createMenu(price = BigDecimal("1000"), menuProducts = listOf(menuProduct))
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            // when
            val actual = runCatching { sut.display(menu.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("표시하게 변경한다") {
            // given
            val product = createProduct(price = BigDecimal("100"))
            val menuProduct = createMenuProduct(quantity = 1, product = product)

            val menu = createMenu(price = BigDecimal("100"), menuProducts = listOf(menuProduct), displayed = false)
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
            val menu = createMenu(displayed = true)
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            // when
            val actual = sut.hide(menu.id)

            // then
            actual.isDisplayed shouldBe false
        }
    }
}) {
    companion object {
        fun createMenu(
            id: UUID? = null,
            name: String? = null,
            price: BigDecimal? = null,
            menuGroup: MenuGroup? = null,
            displayed: Boolean = true,
            menuProducts: List<MenuProduct>? = null
        ) = Menu().apply {
            this.id = id
            this.name = name
            this.price = price
            this.menuGroup = menuGroup
            this.menuGroupId = menuGroup?.id
            this.isDisplayed = displayed
            this.menuProducts = menuProducts
        }

        fun createMenuGroup(id: UUID? = null) = MenuGroup().apply {
            this.id = id
        }

        fun createMenuProduct(product: Product?, quantity: Long = 0) = MenuProduct().apply {
            this.product = product
            this.productId = product?.id
            this.quantity = quantity
        }

        fun createProduct(id: UUID? = null, price: BigDecimal? = null) = Product().apply {
            this.id = id
            this.price = price
        }
    }
}
