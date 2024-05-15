package utils

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.Product
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import utils.spec.MenuGroupSpec
import utils.spec.MenuSpec
import utils.spec.ProductSpec
import java.math.BigDecimal
import java.util.*


fun MockMvc.메뉴생성(): UUID {
    val request = Menu()
    request.menuGroupId = 메뉴그룹생성()

    val menuProduct = MenuProduct()
    menuProduct.productId = 상품생성()
    menuProduct.quantity = 2
    request.menuProducts = listOf(menuProduct)

    request.price = BigDecimal.valueOf(1000)
    request.name = "test menu"
    request.isDisplayed = true

    val mockMvcResponse = perform(
        MockMvcRequestBuilders.post("/api/menus")
            .content(ObjectMapperHolder.objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    ).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated)
        .andReturn()

    return ObjectMapperHolder.objectMapper.readValue(mockMvcResponse.response.contentAsString, Menu::class.java).id
}

fun MockMvc.메뉴그룹생성(request: MenuGroup = MenuGroupSpec.of()): UUID {
    val mockMvcResponse = perform(
        MockMvcRequestBuilders.post("/api/menu-groups")
            .content(ObjectMapperHolder.objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    ).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated)
        .andReturn()

    return ObjectMapperHolder.objectMapper.readValue(mockMvcResponse.response.contentAsString, Product::class.java).id
}

fun MockMvc.상품생성(request: Product = ProductSpec.of()): UUID {
    val mockMvcResponse = perform(
        MockMvcRequestBuilders.post("/api/products")
            .content(ObjectMapperHolder.objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    ).andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated)
        .andReturn()

    return ObjectMapperHolder.objectMapper.readValue(mockMvcResponse.response.contentAsString, Product::class.java).id
}
