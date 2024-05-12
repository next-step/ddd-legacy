package kitchenpos.ui

import com.fasterxml.jackson.databind.ObjectMapper
import kitchenpos.domain.MenuGroup
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class MenuGroupRestControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `신규 메뉴 그룹 생성`() {
        val request = MenuGroup()
        request.name = "test menu group"

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/menu-groups")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isCreated).andExpectAll(
            MockMvcResultMatchers.jsonPath("$.name").value(request.name),
        )
    }
}
