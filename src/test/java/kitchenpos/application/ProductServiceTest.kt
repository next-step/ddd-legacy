package kitchenpos.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductServiceTest(
    @Autowired private val sut: ProductService,
) {
    @DisplayName("등록된 상품 목록을 조회할 수 있다.")
    @Test
    fun find_all_test() {
        // when
        val products = sut.findAll()

        // then
        assertThat(products.size).isEqualTo(6)
    }
}
