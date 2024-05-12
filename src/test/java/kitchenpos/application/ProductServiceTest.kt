package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.FakeMenuRepository
import kitchenpos.domain.FakeProductRepository
import kitchenpos.domain.Product
import kitchenpos.infra.PurgomalumClient
import java.math.BigDecimal

private val productRepository = FakeProductRepository()
private val menuRepository = FakeMenuRepository()
private val purgomalumClient = mockk<PurgomalumClient>()

private val productService = ProductService(productRepository, menuRepository, purgomalumClient)

private fun createProduct() =
    Product().apply {
        name = "후라이드 치킨"
        price = BigDecimal(16000)
    }

class ProductServiceTest : BehaviorSpec({
    given("상품을 생성할 때") {
        `when`("상품의 이름이 null이면") {
            val newProduct = createProduct().apply { name = null }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }

        `when`("상품의 이름에 비속어가 포함되어 있으면") {
            every { purgomalumClient.containsProfanity("비속어") } returns true
            val newProduct = createProduct().apply { name = "비속어" }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }

        `when`("가격이 null이면") {
            val newProduct = createProduct().apply { price = null }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }

        `when`("가격이 0보다 작으면") {
            val newProduct = createProduct().apply { price = BigDecimal(-1) }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
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
