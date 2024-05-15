package kitchenpos.ui

import com.fasterxml.jackson.databind.ObjectMapper
import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import utils.메뉴그룹생성
import utils.메뉴생성
import utils.상품생성
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
class MenuRestControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `신규 메뉴 생성`() {
        val menuGroupId = mockMvc.메뉴그룹생성()
        val productId = mockMvc.상품생성()
        val menuProduct = MenuProduct()
        menuProduct.quantity = 1
        menuProduct.productId = productId

        val request = Menu()
        request.name = "test menu"
        request.isDisplayed = true
        request.menuGroupId = menuGroupId
        request.price = BigDecimal.valueOf(5000)
        request.menuProducts = listOf(menuProduct)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/menus")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isCreated).andExpectAll(
            MockMvcResultMatchers.jsonPath("$.price").value(request.price),
            MockMvcResultMatchers.jsonPath("$.name").value(request.name),
            MockMvcResultMatchers.jsonPath("$.displayed").value(request.isDisplayed)
        )
    }

    @Test
    fun `메뉴의 가격 변경`() {
        val menuId = mockMvc.메뉴생성()
        val request = Menu()
        request.price = BigDecimal.valueOf(13300)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/menus/$menuId/price")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk).andExpectAll(
            MockMvcResultMatchers.jsonPath("$.price").value(request.price),
        )
    }

    @Test
    fun `메뉴의 전시상태 종료 or 활성화`() {
        val menuId = mockMvc.메뉴생성()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/menus/$menuId/hide")
        ).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk).andExpectAll(
            MockMvcResultMatchers.jsonPath("$.displayed").value(false),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/menus/$menuId/display")
        ).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk).andExpectAll(
            MockMvcResultMatchers.jsonPath("$.displayed").value(true),
        )
    }

}
