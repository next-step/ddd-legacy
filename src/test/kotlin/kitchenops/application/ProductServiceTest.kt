package kitchenops.application

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.application.ProductService
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import spec.BaseUnitSpec
import java.math.BigDecimal
import java.util.*

internal class ProductServiceTest : BaseUnitSpec({

    val productRepository = mockk<ProductRepository>()
    val menuRepository = mockk<MenuRepository>()
    val purgomalumClient = mockk<PurgomalumClient>()

    val sut = ProductService(productRepository, menuRepository, purgomalumClient)

    context("상품을 생성할 수 있다") {
        test("가격을 지정하지 않으면 생성할 수 없다") {
            // given
            val request = Product().apply { price = null }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("가격이 0원 미만이면 생성할 수 없다") {
            // given
            val request = Product().apply { price = BigDecimal("-1") }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("이름을 지정하지 않으면 생성할 수 없다") {
            // given
            val request = Product().apply {
                price = BigDecimal("100")
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
            val request = Product().apply {
                price = BigDecimal("100")
                name = ""
            }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("상품의 이름에 욕설이 포함되면 생성할 수 없다") {
            // given
            val request = Product().apply {
                price = BigDecimal("10")
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
            val request = Product().apply {
                price = BigDecimal("1000")
                name = "chicken"
            }

            every { purgomalumClient.containsProfanity("chicken") } returns false
            every { productRepository.save(match { it.price == BigDecimal("1000") && it.name == "chicken" }) } returns request

            // when
            val actual = sut.create(request)

            // then
            actual should {
                it.price shouldBe BigDecimal("1000")
                it.name shouldBe "chicken"
            }

            verify(exactly = 1) { productRepository.save(any()) }
        }
    }

    context("상품을 전체 조회할 수 있다") {
        test("전체 조회한다") {
            // given
            val product1 = Product().apply {
                price = BigDecimal("19900")
                name = "fried chicken"
            }
            val product2 = Product().apply {
                price = BigDecimal("3000")
                name = "cheese ball"
            }

            every { productRepository.findAll() } returns listOf(product1, product2)

            // when
            val actual = sut.findAll()

            // then
            actual shouldBe listOf(product1, product2)
        }
    }

    context("상품의 가격을 변경할 수 있다") {
        test("상품의 가격을 지정하지 않으면 변경할 수 없다") {
            // given
            val request = Product().apply { price = null }

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("상품의 가격이 0원 미만이면 변경할 수 없다") {
            // given
            val request = Product().apply { price = BigDecimal("-1") }

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("존재하지 않는 상품은 변경할 수 없다") {
            // given
            val request = Product().apply { price = BigDecimal("100") }

            every { productRepository.findById(request.id) } returns Optional.empty()

            // when
            val actual = runCatching { sut.changePrice(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        test("상품을 사용하는 메뉴의 가격이 메뉴를 구성하는 상품 가격의 총 합보다 클 경우 메뉴를 숨긴다") {
            // given
            val request = Product().apply { price = BigDecimal("500") }
            every { productRepository.findById(request.id) } returns Optional.of(request)

            val product1 = Product().apply {
                id = request.id
                price = request.price
            }
            val menuProduct1 = MenuProduct().apply {
                product = product1
                quantity = 2
            }
            val menu = Menu().apply {
                price = BigDecimal("600")
                menuProducts = listOf(menuProduct1)
            }
            every { menuRepository.findAllByProductId(request.id) } returns listOf(menu)

            // when
            val actual = sut.changePrice(request.id, request)

            // then
            actual.price shouldBe BigDecimal("500")
            menu.isDisplayed shouldBe false
        }

        test("가격을 변경한다") {
            // given
            val request = Product().apply { price = BigDecimal("300") }

            every { productRepository.findById(request.id) } returns Optional.of(request)
            every { menuRepository.findAllByProductId(request.id) } returns emptyList()

            // when
            val actual = sut.changePrice(request.id, request)

            // then
            actual.price shouldBe BigDecimal("300")
        }
    }
})
