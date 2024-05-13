package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.math.BigDecimal
import java.util.UUID
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient

class ProductServiceCreateTest : ShouldSpec({
    val productRepository = mockk<ProductRepository>()
    val menuRepository = mockk<MenuRepository>()
    val purgomalumClient = mockk<PurgomalumClient>()
    val service = ProductService(
        productRepository,
        menuRepository,
        purgomalumClient
    )

    val productSlot = slot<Product>()

    beforeTest {
        clearMocks(
            productRepository,
            menuRepository,
            purgomalumClient
        )

        every { productRepository.save(capture(productSlot)) } answers { productSlot.captured }
        every { purgomalumClient.containsProfanity(any()) } returns false
//        menuRepository
    }

    context("상품 생성") {
        should("성공") {
            // given
            val request = createProduct(
                id = null
            )

            // when
            val product = service.create(request)

            // then
            product.id.shouldNotBeNull()
            product.name shouldBe request.name
            product.price shouldBe request.price
        }

        should("실패 - 상품 가격이 0원 미만인 경우") {
            // given
            val request = createProduct(
                id = null,
                price = (-1000).toBigDecimal()
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 상품명이 비어있는 경우") {
            // given
            val request = createProduct(
                id = null,
                name = null
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 상품명에 욕설이 포함된 경우") {
            // given
            val request = createProduct(
                id = null
            )
            every { purgomalumClient.containsProfanity(any()) } returns true

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
        private fun createProduct(
            id: UUID? = UUID.randomUUID(),
            name: String? = "test-product-name",
            price: BigDecimal? = 1000.toBigDecimal()
        ): Product {
            return Product().apply {
                setId(id)
                setName(name)
                setPrice(price)
            }
        }
    }
}