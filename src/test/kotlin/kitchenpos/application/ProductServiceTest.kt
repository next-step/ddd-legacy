package kitchenpos.application

import kitchenpos.infra.PurgomalumClient
import kitchenpos.product.ProductFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class ProductServiceTest {
    @MockBean
    private lateinit var purgomalumClient: PurgomalumClient

    @Autowired
    private lateinit var productService: ProductService

    @Test
    @DisplayName("상품이름이 비속어 필터링을 통과하면 상품이 생성")
    fun createProduct() {
        // given
        val product = ProductFixture.fixture(name = "상품")
        given(purgomalumClient.containsProfanity(product.name)).willReturn(false)

        // when
        val createdProduct = productService.create(product)

        // then
        assertThat(createdProduct).isNotNull
    }

    @Test
    @DisplayName("상품이름이 비속어 필터링을 탈락하면 상품이 생성실패")
    fun preventBadWord() {
        // given
        val product = ProductFixture.fixture(name = "욕설")
        given(purgomalumClient.containsProfanity(product.name)).willReturn(true)

        // when then
        assertThatIllegalArgumentException().isThrownBy {
            productService.create(product)
        }
    }


}
