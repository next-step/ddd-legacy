package kitchenpos.application

import io.kotest.assertions.assertSoftly
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
            val 가격_정보가_없는_요청 = getProduct(
                name = "가격 정보가 없는 상품",
                price = null,
            )

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.create(가격_정보가_없는_요청)
            }
        }

        @DisplayName("상품의 이름 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test2() {
            // given
            val 이름_정보가_없는_요청 = getProduct(
                name = null,
            )

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.create(이름_정보가_없는_요청)
            }
        }

        @DisplayName("상품의 이름에 욕설이 포함된다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test3() {
            // given
            val 욕설이_포함된_상품 = getProduct(
                name = "욕설이 포함된 상품",
            )

            every { purgomalumClient.containsProfanity(any()) } returns true

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.create(욕설이_포함된_상품)
            }
        }

        @DisplayName("상품의 가격이 0원 미만이라면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test4() {
            // given
            val 가격이_음수인_요청 = getProduct(
                name = "가격이 음수인 상품",
                price = BigDecimal.valueOf(-1),
            )

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.create(가격이_음수인_요청)
            }
        }

        @DisplayName("상품의 요청이 정상적인 경우, 상품이 정상적으로 저장된다.")
        @ParameterizedTest
        @ValueSource(strings = ["", " ", "테스트 상품"])
        fun test5(name: String) {
            // given
            val 정상_요청 = getProduct(
                name = name,
                price = BigDecimal.valueOf(1000L),
            )

            every { purgomalumClient.containsProfanity(any()) } returns false
            every { productRepository.save(any()) } returns 정상_요청

            // when
            val result = productService.create(정상_요청)

            // then
            assertSoftly {
                result.id shouldBe 정상_요청.id
                result.name shouldBe 정상_요청.name
                result.price shouldBe 정상_요청.price
            }
        }
    }

    @Nested
    inner class `상품 가격 변경 테스트` {
        @DisplayName("변경 하려는 가격의 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val productId = UUID.randomUUID()
            val 가격_정보가_없는_요청 = getProduct(
                productId = productId,
                name = "가격 정보가 없는 상품",
            )

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.changePrice(productId, 가격_정보가_없는_요청)
            }
        }

        @DisplayName("변경 하려는 가격이 0원 미만이라면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test2() {
            // given
            val productId = UUID.randomUUID()
            val 가격이_음수인_요청 = getProduct(
                productId = productId,
                name = "가격이 음수인 상품",
                price = BigDecimal.valueOf(-1L)
            )

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                productService.changePrice(productId, 가격이_음수인_요청)
            }
        }

        @DisplayName("요청한 id의 상품이 존재하지 않는다면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test3() {
            // given
            val productId = UUID.randomUUID()
            val 존재_하지_않는_상품_요청 = getProduct(
                productId = UUID.randomUUID(),
                name = "존재하지 않는 상품",
            )

            every { productRepository.findById(any()) } throws NoSuchElementException()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                productService.changePrice(productId, 존재_하지_않는_상품_요청)
            }
        }

        @DisplayName("요청한 id의 상품이 등록된 메뉴의 가격이 새로 계산한 값보다 더 클 때 해당 메뉴는 노출되지 않도록 설정되고, 요청한 가격으로 변경되어야 한다.")
        @Test
        fun test4() {
            // given
            val productId = UUID.randomUUID()
            val 메뉴에_속해_있는_상품 = getProduct(
                productId = productId,
                name = "메뉴에 속해 있는 상품",
                price = BigDecimal.valueOf(1000L)
            )

            val 찾아온_상품 = getProduct(
                productId = 메뉴에_속해_있는_상품.id,
                name = 메뉴에_속해_있는_상품.name,
                price = 메뉴에_속해_있는_상품.price
            )

            val menu = Menu().apply {
                this.price = 메뉴에_속해_있는_상품.price + BigDecimal.ONE
                this.menuProducts = listOf(MenuProduct().apply {
                    this.seq = 1L
                    this.productId = productId
                    this.product = 찾아온_상품
                    this.quantity = 1
                })
                this.isDisplayed = true
            }

            every { productRepository.findById(any()) } returns Optional.of(찾아온_상품)
            every { menuRepository.findAllByProductId(productId) } returns listOf(menu)

            // when
            val result = productService.changePrice(productId, 메뉴에_속해_있는_상품)

            // then
            result.price shouldBe 메뉴에_속해_있는_상품.price
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

    private fun getProduct(
        productId: UUID = UUID.randomUUID(),
        name: String? = "상품 이름",
        price: BigDecimal? = BigDecimal.valueOf(1000L),
    ) = Product().apply {
        this.id = productId
        this.name = name
        this.price = price
    }
}
