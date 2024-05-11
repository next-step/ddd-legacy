package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kitchenpos.domain.*
import kitchenpos.infra.PurgomalumClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
        @ParameterizedTest
        @ValueSource(strings = ["", " ", "테스트 상품"])
        fun test5(name: String) {
            // given
            val request = Product().apply {
                this.id = UUID.randomUUID()
                this.price = BigDecimal.valueOf(1000L)
                this.name = name
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

    @Nested
    inner class `상품 가격 변경 테스트` {
        @DisplayName("변경 하려는 가격의 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val productId = UUID.randomUUID()
            val request = Product().apply {
                this.name = "테스트 상품"
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.changePrice(productId, request)
            }
        }

        @DisplayName("변경 하려는 가격이 0원 미만이라면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test2() {
            // given
            val productId = UUID.randomUUID()
            val request = Product().apply {
                this.name = "테스트 상품"
                this.price = BigDecimal.valueOf(-1L)
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.changePrice(productId, request)
            }
        }

        @DisplayName("요청한 id의 상품이 존재하지 않는다면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test3() {
            // given
            val productId = UUID.randomUUID()
            val request = Product().apply {
                this.name = "테스트 상품"
                this.price = BigDecimal.valueOf(1000L)
            }

            every { productRepository.findById(any()) } throws NoSuchElementException()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                productService.changePrice(productId, request)
            }
        }

        @DisplayName("요청한 id의 상품이 등록된 메뉴의 가격이 새로 계산한 값보다 더 클 때 해당 메뉴는 노출되지 않도록 설정되고, 요청한 가격으로 변경되어야 한다.")
        @Test
        fun test4() {
            // given
            val productId = UUID.randomUUID()
            val request = Product().apply {
                this.name = "테스트 상품"
                this.price = BigDecimal.valueOf(1000L)
            }

            val foundProduct = Product().apply {
                this.id = productId
                this.name = request.name
                this.price = request.price
            }

            val menu = Menu().apply {
                this.price = request.price + BigDecimal.ONE
                this.menuProducts = listOf(MenuProduct().apply {
                    this.seq = 1L
                    this.productId = productId
                    this.product = foundProduct
                    this.quantity = 1
                })
                this.isDisplayed = true
            }

            every { productRepository.findById(any()) } returns Optional.of(foundProduct)
            every { menuRepository.findAllByProductId(productId) } returns listOf(menu)

            // when
            val result = productService.changePrice(productId, request)

            // then
            result.price shouldBe request.price
            menu.isDisplayed shouldBe false
        }

        @DisplayName("요청한 id의 상품이 등록된 메뉴의 가격이 새로 계산한 값보다 더 작다면 해당 메뉴의 노출 설정은 유지되고, 요청한 가격으로 변경되어야 한다.")
        @Test
        fun test5() {
            // given
            val productId = UUID.randomUUID()
            val request = Product().apply {
                this.name = "테스트 상품"
                this.price = BigDecimal.valueOf(1000L)
            }

            val foundProduct = Product().apply {
                this.id = productId
                this.name = request.name
                this.price = request.price
            }

            val menu = Menu().apply {
                this.price = request.price - BigDecimal.ONE
                this.menuProducts = listOf(MenuProduct().apply {
                    this.seq = 1L
                    this.productId = productId
                    this.product = foundProduct
                    this.quantity = 1
                })
                this.isDisplayed = true
            }

            every { productRepository.findById(any()) } returns Optional.of(foundProduct)
            every { menuRepository.findAllByProductId(productId) } returns listOf(menu)

            // when
            val result = productService.changePrice(productId, request)

            // then
            result.price shouldBe request.price
            menu.isDisplayed shouldBe true
        }
    }
}
