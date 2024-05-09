package kitchenpos.application

import kitchenpos.domain.*
import kitchenpos.infra.PurgomalumClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockitoExtension::class)
class ProductServiceMockTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var menuRepository: MenuRepository

    @Mock
    private lateinit var purgomalumClient: PurgomalumClient

    @InjectMocks
    private lateinit var productService: ProductService


    @Test
    fun `상품 생성 요청 - 정상적인 상품 생성 요청 성공`() {
        val request = Product()
        request.name = "치킨"
        request.price = BigDecimal.TEN

        `상품명에 비속어 혹은 욕설 없음`(request.name)

        Assertions.assertThatCode { productService.create(request) }
            .doesNotThrowAnyException()
    }

    @Test
    fun `상품 생성 요청 - 상품명이 없는 경우 실패`() {
        val request = Product()
        request.price = BigDecimal.TEN

        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { productService.create(request) }
    }

    @Test
    fun `상품 생성 요청 - 상품명에 비속어 or 욕설이 포함된 경우 실패`() {
        val request = Product()
        request.name = "비속어 or 욕설"
        request.price = BigDecimal.TEN

        `상품명에 비속어 혹은 욕설 포함됨`(request.name)

        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { productService.create(request) }
    }

    @Test
    fun `상품 생성 요청 - 상품의 가격이 없는 경우 실패`() {
        val request = Product()
        request.name = "치킨"

        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { productService.create(request) }
    }

    @Test
    fun `상품 생성 요청 - 상품의 가격이 0보다 작은 경우 실패`() {
        val request = Product()
        request.name = "치킨"
        request.price = BigDecimal.valueOf(-10000)

        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { productService.create(request) }
    }

    @Test
    fun `상품 가격 변경 요청 - 요청 상품 가격이 0보다 작은 경우 실패함`() {
        //given
        val productId = UUID.randomUUID()
        val request = Product()
        request.price = BigDecimal.valueOf(-9999)

        //when & then
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { productService.changePrice(productId, request) }
    }

    @Test
    fun `상품 가격 변경 요청 - 메뉴의 가격이 메뉴상품의 가격 * 재고 합이 보다 큰 경우 메뉴는 전시 종료`() {
        //given
        val productId = UUID.randomUUID()
        val request = Product()
        request.price = BigDecimal.valueOf(15000)

        val menu = Menu()
        menu.isDisplayed = true

        val menuProduct = MenuProduct()
        menuProduct.quantity = 2
        menuProduct.product = request

        menu.price = BigDecimal.valueOf(35000)
        menu.menuProducts = listOf(menuProduct)

        `when`(productRepository.findById(productId))
            .thenReturn(Optional.ofNullable(Product()))
        `when`(menuRepository.findAllByProductId(productId))
            .thenReturn(listOf(menu))

        //when & then
        Assertions.assertThatCode { productService.changePrice(productId, request) }
            .doesNotThrowAnyException()
        Assertions.assertThat(menu.isDisplayed).isEqualTo(false)
    }

    private fun `상품명에 비속어 혹은 욕설 없음`(name: String) {
        `when`(purgomalumClient.containsProfanity(name))
            .thenReturn(false)
    }


    private fun `상품명에 비속어 혹은 욕설 포함됨`(name: String) {
        `when`(purgomalumClient.containsProfanity(name))
            .thenReturn(true)
    }
}
