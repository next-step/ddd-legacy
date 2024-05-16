package kitchenpos.application

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.ProductRepository
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeProductRepository

class ProductServiceFindAllServiceTest : ShouldSpec({
    lateinit var productRepository: ProductRepository
    lateinit var menuRepository: MenuRepository
    lateinit var service: ProductService

    beforeTest {
        productRepository = FakeProductRepository()
        menuRepository = FakeMenuRepository()

        productRepository.save(mockk {
            every { id } returns UUID.randomUUID()
        })
        productRepository.save(mockk {
            every { id } returns UUID.randomUUID()
        })

        service = ProductService(
            productRepository,
            menuRepository,
            mockk()
        )
    }

    context("상품 목록 조회") {
        should("성공") {
            // when
            val products = service.findAll()

            // then
            products.size shouldBe 2
        }
    }
})