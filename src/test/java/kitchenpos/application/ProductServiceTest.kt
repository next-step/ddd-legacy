package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ProductServiceTest {

    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var menuRepository: MenuRepository

    @MockK
    private lateinit var purgomalumClient: PurgomalumClient

    @InjectMockKs
    private lateinit var productService: ProductService

    @Nested
    inner class `상품 등록 테스트` {
        @DisplayName("상품의 가격 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val request = Product().apply {
                this.id = UUID.randomUUID()
                this.name = "테스트 상품"
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.create(request)
            }
        }

        @DisplayName("상품의 이름 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test2() {
            // given
            val request = Product().apply {
                this.id = UUID.randomUUID()
                this.price = BigDecimal.valueOf(1000L)
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.create(request)
            }
        }

        @DisplayName("상품의 이름에 욕설이 포함된다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test3() {
            // given
            val request = Product().apply {
                this.id = UUID.randomUUID()
                this.price = BigDecimal.valueOf(1000L)
                this.name = "욕설"
            }

            every { purgomalumClient.containsProfanity(any()) } returns true

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.create(request)
            }
        }

        @DisplayName("상품의 가격이 0원 미만이라면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test4() {
            // given
            val request = Product().apply {
                this.id = UUID.randomUUID()
                this.price = BigDecimal.valueOf(-1)
                this.name = "테스트 상품"
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.create(request)
            }
        }

        @DisplayName("상품의 요청이 정상적인 경우, 상품이 정상적으로 저장된다.")
        @Test
        fun test5() {
            // given
            val request = Product().apply {
                this.id = UUID.randomUUID()
                this.price = BigDecimal.valueOf(1000L)
                this.name = "테스트 상품"
            }

            every { purgomalumClient.containsProfanity(any()) } returns false
            every { productRepository.save(any()) } returns request

            // when
            val result = productService.create(request)

            // then
            result.id shouldBe request.id
            result.name shouldBe request.name
            result.price shouldBe request.price
        }
    }
}
