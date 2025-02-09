package kitchenpos.application

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import random.randomProduct
import random.randomProductId
import java.math.BigDecimal
import java.util.*

class ProductServiceTest {
    private val productRepository: ProductRepository = mockk()
    private val menuRepository: MenuRepository = mockk()
    private val purgomalumClient: PurgomalumClient = mockk()
    private lateinit var productService: ProductService

    @BeforeEach
    fun setUp() {
        productService = ProductService(productRepository, menuRepository, purgomalumClient)
    }

    @Nested
    inner class `상품 생성시` {
        @Test
        fun `가격이 널인 경우 예외 발생`() {
            // given
            val product = randomProduct(price = null)
            // when then
            assertThrows<IllegalArgumentException> {
                productService.create(product)
            }
        }

        @Test
        fun `가격이 음수인 경우 예외 발생`() {
            // given
            val product = randomProduct(price = BigDecimal.valueOf(-1))
            // when then
            assertThrows<IllegalArgumentException> {
                productService.create(product)
            }
        }

        @Test
        fun `상품명이 널인 경우 예외 발생`() {
            // given
            val product = randomProduct(name = null)
            // when then
            assertThrows<IllegalArgumentException> {
                productService.create(product)
            }
        }

        @Test
        fun `상품명이 비속어가 포함될 경우 예외 발생`() {
            // given
            val product = randomProduct()
            every { purgomalumClient.containsProfanity(product.name) } returns true

            // when then
            assertThrows<IllegalArgumentException> {
                productService.create(product)
            }
        }

        @Test
        fun `정상 생성`() {
            // given
            val product = randomProduct()
            every { purgomalumClient.containsProfanity(product.name) } returns false
            every { productRepository.save(any()) } returns product
            // when
            val expected = productService.create(product)

            // then
            expected shouldBe product
        }
    }

    @Nested
    inner class `상품 가격 변경시` {
        @Test
        fun `변경 요청 가격이 널인 경우 예외 발생`() {
            // given
            val product = randomProduct(price = null)
            val productId = randomProductId()

            // when then
            assertThrows<IllegalArgumentException> {
                productService.changePrice(productId, product)
            }
        }

        @Test
        fun `변경 요청 가격이 음수인 경우 예외 발생`() {
            // given
            val product = randomProduct(price = BigDecimal.valueOf(-1))
            val productId = randomProductId()

            // when then
            assertThrows<IllegalArgumentException> {
                productService.changePrice(productId, product)
            }
        }

        @Test
        fun `존재하지 않는 상품인 경우 예외 발생`() {
            // given
            val product = randomProduct()
            val productId = randomProductId()
            every { productRepository.findById(productId) } returns Optional.empty()
            // when then
            assertThrows<NoSuchElementException> {
                productService.changePrice(productId, product)
            }
        }

        @Test
        fun `메뉴 가격이 상품의 합산 가격보다 클 경우, 해당 메뉴 상품을 비노출 처리`() {
            // given
            val productId = UUID.randomUUID()
            val price = BigDecimal.valueOf(200)
            val requestProduct = randomProduct(id = productId, price = price)
            every { productRepository.findById(productId) } returns Optional.of(randomProduct(id = productId, price = BigDecimal.valueOf(100)))

            val menu = mockk<Menu>(relaxed = true)
            val menuProduct = mockk<MenuProduct>()
            val menuProducts = listOf(menuProduct)
            every { menuRepository.findAllByProductId(productId) } returns listOf(menu)

            every { menuProduct.product } returns requestProduct
            every { menuProduct.quantity } returns 1
            every { menu.price } returns BigDecimal.valueOf(250) // Menu price greater than product total price
            every { menu.menuProducts } returns menuProducts

            // when
            productService.changePrice(productId, requestProduct)

            // then
            verify { menu.setDisplayed(false) }
        }
    }

    @Test
    fun `상품 전체 조회`() {
        // given
        val products = listOf(randomProduct(), randomProduct(), randomProduct())
        every { productRepository.findAll() } returns products

        // when then
        productService.findAll() shouldBe products
    }
}
