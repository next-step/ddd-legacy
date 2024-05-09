package kitchenpos.ui

import com.fasterxml.jackson.databind.ObjectMapper
import kitchenpos.domain.Product
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Sql("classpath:/db/migration/V2__Insert_default_data.sql")
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
        val productId = "3b528244-34f7-406b-bb7e-690912f66b10"
        val price = 20000L
        val request = Product()
        request.price = BigDecimal.valueOf(price)

        mockMvc.perform(
            put("/api/products/$productId/price")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk).andExpectAll(jsonPath("$.price").value(price))
    }

    @Test
    fun `모든 상품 목록 조회`() {
        mockMvc.perform(get("/api/products").contentType(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk).andExpectAll(jsonPath("$.length()").value(6))
    }
}
