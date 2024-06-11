package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.util.UUID
import kitchenpos.domain.Product
import kitchenpos.infra.PurgomalumClient
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeProductRepository
import kitchenpos.testsupport.ProductFixtures.createProduct

class ProductServiceCreateTest : ShouldSpec({
    lateinit var purgomalumClient: PurgomalumClient
    lateinit var service: ProductService

    beforeTest {
        purgomalumClient = mockk {
            every { containsProfanity(any()) } returns false
        }
        service = ProductService(
            FakeProductRepository(),
            FakeMenuRepository(),
            purgomalumClient
        )
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
})
