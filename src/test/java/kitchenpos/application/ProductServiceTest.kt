package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import java.math.BigDecimal

class ProductServiceTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val menuRepository = mockk<MenuRepository>()
    val purgomalumClient = mockk<PurgomalumClient>()

    val productService = ProductService(productRepository, menuRepository, purgomalumClient)

    given("상품을 생성할 때") {
        `when`("상품의 이름이 null이면") {
            val newProduct =
                Product().apply {
                    name = null
                    price = BigDecimal(10000)
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }

        `when`("상품의 이름에 비속어가 포함되어 있으면") {
            every { purgomalumClient.containsProfanity("비속어") } returns true
            val newProduct =
                Product().apply {
                    name = "비속어"
                    price = BigDecimal(10000)
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }

        `when`("가격이 null이면") {
            val newProduct =
                Product().apply {
                    name = "새상품"
                    price = null
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }

        `when`("가격이 0보다 작으면") {
            val newProduct =
                Product().apply {
                    name = "새상품"
                    price = BigDecimal(-1)
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    productService.create(newProduct)
                }
            }
        }
    }

    given("상품을 조회할 때") {
        `when`("상품이 존재하지 않으면") {
            every { productRepository.findAll() } returns emptyList()

            then("빈 목록을 반환한다.") {
                val results = productService.findAll()
                results.size shouldBe 0
            }
        }
    }
})
