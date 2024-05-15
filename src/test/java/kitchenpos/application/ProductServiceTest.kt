package kitchenpos.application

import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Product
import kitchenpos.infra.PurgomalumClient
import kitchenpos.utils.generateUUIDFrom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.math.BigDecimal

@SpringBootTest
class ProductServiceTest {
    @MockBean
    private lateinit var purgomalumCloneable: PurgomalumClient

    @Autowired
    private lateinit var menuRepository: MenuRepository

    @Autowired
    private lateinit var sut: ProductService

    @DisplayName("등록된 상품 목록을 조회할 수 있다.")
    @Test
    fun find_all_products_test() {
        // when
        val products = sut.findAll()

        // then
        assertThat(products.size).isGreaterThan(0)
    }

    @DisplayName("상품 가격을 꼭 입력해야 한다.")
    @Test
    fun create_product_test1() {
        // given
        val request = Product()

        // when
        // then
        assertThrows<IllegalArgumentException> { sut.create(request) }
    }

    @DisplayName("가격은 음수일수 없다.")
    @Test
    fun create_product_test2() {
        // given
        val request = Product()
        request.price = BigDecimal.valueOf(-1000)
        // when
        // then
        assertThrows<IllegalArgumentException> { sut.create(request) }
    }

    @DisplayName("상품 이름을 꼭 입력해야 한다.")
    @Test
    fun create_product_test3() {
        // given
        val request = Product()
        request.price = BigDecimal.valueOf(1000)

        // when
        // then
        assertThrows<IllegalArgumentException> { sut.create(request) }
    }

    @DisplayName("상품 이름에는 비속어가 들어갈 수 없다.")
    @Test
    fun create_product_test4() {
        // given
        val slang = "비속어"
        val request = Product()
        request.price = BigDecimal.valueOf(1000)
        request.name = slang
        given(purgomalumCloneable.containsProfanity(slang)).willReturn(true)

        // when
        // then
        assertThrows<IllegalArgumentException> { sut.create(request) }
    }

    @DisplayName("상품 등록이 가능하다.")
    @Test
    fun create_product_test5() {
        // given
        val request = Product()
        request.price = BigDecimal.valueOf(1000)
        request.name = "후라이드 치킨"
        given(purgomalumCloneable.containsProfanity(any())).willReturn(false)

        // when
        val createdProduct = sut.create(request)

        // then
        assertThat(createdProduct.id).isNotNull()
    }

    @DisplayName("변경하려는 가격은 음수일 수 없다.")
    @Test
    fun change_price_test1() {
        // given
        val productId = generateUUIDFrom(uuidWithoutDash = "3b52824434f7406bbb7e690912f66b10")
        val request = Product()
        request.price = BigDecimal.valueOf(-1000)

        // when
        // then
        assertThrows<IllegalArgumentException> { sut.changePrice(productId, request) }
    }

    @DisplayName("등록된 상품의 가격을 수정할 수 있다.")
    @Test
    fun change_price_test2() {
        // given
        val productId = generateUUIDFrom(uuidWithoutDash = "3b52824434f7406bbb7e690912f66b10")
        val targetPrice = BigDecimal.valueOf(17_000)
        val request = Product()
        request.price = targetPrice

        // when
        val changedProduct = sut.changePrice(productId, request)

        // then
        assertThat(changedProduct.price).isEqualTo(targetPrice)
    }

    @DisplayName("해당 상품으로 구성된 메뉴의 가격이 수정된 상품의 가격 총합보다 크면 메뉴를 비노출한다.")
    @Test
    fun change_price_test3() {
        // given
        val productId = generateUUIDFrom(uuidWithoutDash = "3b52824434f7406bbb7e690912f66b10")
        val targetPrice = BigDecimal.valueOf(13_000)
        val request = Product()
        request.price = targetPrice

        // when
        val changedProduct = sut.changePrice(productId, request)

        // then
        val menu =
            menuRepository.findAllByProductId(productId)
                .first { menu -> menu.price > changedProduct.price }
        assertThat(menu.isDisplayed).isFalse()
    }
}
