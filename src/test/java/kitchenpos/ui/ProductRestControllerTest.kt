package kitchenpos.ui

import com.fasterxml.jackson.databind.ObjectMapper
import kitchenpos.domain.Product
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utils.상품생성
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
class ProductRestControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `신규 상품 생성`() {
        val price = 20000L
        val name = "test"
        val request = Product()
        request.price = BigDecimal.valueOf(price)
        request.name = name

        mockMvc.perform(
            post("/api/products")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isCreated).andExpectAll(
            jsonPath("$.price").value(price),
            jsonPath("$.name").value(name)
        )
    }

    @Test
    fun `상품의 가격 변경`() {
        val product = Product()
        product.name = "치킨"
        product.price = BigDecimal.valueOf(15000)
        val productId = mockMvc.상품생성(product)
        val request = Product()
        request.price = BigDecimal.valueOf(10000)

        mockMvc.perform(
            put("/api/products/$productId/price")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk).andExpectAll(jsonPath("$.price").value(request.price))
    }
}
