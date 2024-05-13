package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.FakeMenuRepository
import kitchenpos.domain.FakeProductRepository
import kitchenpos.domain.Product
import kitchenpos.infra.PurgomalumClient
import java.math.BigDecimal
import java.util.UUID

private val productRepository = FakeProductRepository()
private val menuRepository = FakeMenuRepository()
private val purgomalumClient = mockk<PurgomalumClient>()

private val productService = ProductService(productRepository, menuRepository, purgomalumClient)

private val Int.won: BigDecimal get() = this.toBigDecimal()

private infix fun String?.costs(amount: BigDecimal?): Product {
    return Product().apply {
        this.id = UUID.randomUUID()
        this.name = this@costs
        this.price = amount
    }
}

class ProductServiceTest : BehaviorSpec({
    given("상품을 생성할 때") {
        `when`("입력 값이 정상이면") {
            every { purgomalumClient.containsProfanity("후라이드 치킨") } returns false
            val newProduct = "후라이드 치킨" costs 10000.won

            then("정상적으로 생성된다.") {
                with(productService.create(newProduct)) {
                    this.id shouldNotBe null
                    this.name shouldBe "후라이드 치킨"
                    this.price shouldBe 10000.won
                }
            }
        }

        `when`("상품의 이름이 null이면") {
            val newProduct = null costs 10000.won

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }

        `when`("상품의 이름에 비속어가 포함되어 있으면") {
            every { purgomalumClient.containsProfanity("비속어") } returns true
            val newProduct = "비속어" costs 10000.won

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }

        `when`("가격이 null이면") {
            val newProduct = "후라이드 치킨" costs null

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }

        `when`("가격이 0보다 작으면") {
            val newProduct = "후라이드 치킨" costs (-1).won

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }
    }

    given("가격을 변경할 떄") {
        `when`("메뉴 가격이 null이면") {
            val newProduct = "후라이드 치킨" costs null

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.changePrice(newProduct.id, newProduct)
                }
            }
        }

        `when`("메뉴의 가격이 0보다 작으면") {
            val newProduct = "후라이드 치킨" costs (-1).won

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.changePrice(newProduct.id, newProduct)
                }
            }
        }

        `when`("상품이 존재하지 않으면") {
            val newProduct = "후라이드 치킨" costs 10000.won

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    productService.changePrice(newProduct.id, newProduct)
                }
            }
        }
    }

    given("상품을 조회할 때") {
        `when`("상품이 존재하지 않으면") {
            productRepository.clear()

            then("빈 목록을 반환한다.") {
                val results = productService.findAll()
                results.size shouldBe 0
            }
        }
    }
})
