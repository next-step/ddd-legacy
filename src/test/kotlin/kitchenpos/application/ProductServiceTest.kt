package kitchenpos.application

import domain.MenuFixtures.makeMenuOne
import domain.ProductFixtures.makeProductOne
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*

class ProductServiceTest : DescribeSpec() {
    init {
        describe("ProductService 클래스의") {
            val productRepository = mockk<ProductRepository>()
            val menuRepository = mockk<MenuRepository>()
            val purgomalumClient = mockk<PurgomalumClient>()
            val productService = ProductService(productRepository, menuRepository, purgomalumClient)

            describe("create 메서드는") {
                context("정상적인 상품 요청이 주어졌을 때") {
                    it("상품을 생성한다") {
                        val product =
                            Product().apply {
                                this.name = "Test Product"
                                this.price = BigDecimal(1000)
                            }

                        every { productRepository.save(any()) } returns product
                        every { purgomalumClient.containsProfanity(any()) } returns false

                        val result = productService.create(product)

                        result.name shouldBe product.name
                        result.price shouldBe product.price
                    }
                }

                context("비속어가 포함된 상품 요청이 주어졌을 때") {
                    it("IllegalArgumentException을 던진다") {
                        val product =
                            Product().apply {
                                this.name = "Bad Word"
                                this.price = BigDecimal(1000)
                            }

                        every { purgomalumClient.containsProfanity(any()) } returns true

                        assertThrows<IllegalArgumentException> {
                            productService.create(product)
                        }
                    }
                }
            }

            describe("changePrice 메서드는") {
                context("상품 아이디와 가격이 주어졌을떄") {
                    it("상품의 가격을 변경한다") {
                        val product = makeProductOne()

                        val newPrice = BigDecimal(15000)

                        every { productRepository.findById(any()) } returns Optional.of(product)
                        every { productRepository.save(any()) } returns product.apply { this.price = newPrice }
                        every { menuRepository.findAllByProductId(any()) } returns listOf(makeMenuOne())

                        val result = productService.changePrice(product.id, product.apply { this.price = newPrice })

                        result.price shouldBe newPrice
                    }

                    it("메뉴 상품 가격들의 합이 메뉴보다 크면, 메뉴를 비노출 처리한다.") {
                        val product = makeProductOne()

                        val newPrice = BigDecimal(20000)

                        every { productRepository.findById(any()) } returns Optional.of(product)
                        every { productRepository.save(any()) } returns product.apply { this.price = newPrice }

                        val menu = makeMenuOne().apply { this.price = BigDecimal(20000) }

                        every {
                            menuRepository.findAllByProductId(
                                any(),
                            )
                        } returns listOf(menu)

                        productService.changePrice(product.id, product.apply { this.price = newPrice })

                        menu.isDisplayed shouldBe false
                    }
                }

                context("가격이 0보다 작은 가격 변경 요청이 주어졌을 때") {
                    it("IllegalArgumentException을 던진다") {
                        val product =
                            Product().apply {
                                this.id = UUID.randomUUID()
                                this.name = "Test Product"
                                this.price = BigDecimal(1000)
                            }

                        val newPrice = BigDecimal(-1000)

                        assertThrows<IllegalArgumentException> {
                            productService.changePrice(product.id, product.apply { this.price = newPrice })
                        }
                    }
                }
            }
        }
    }
}
