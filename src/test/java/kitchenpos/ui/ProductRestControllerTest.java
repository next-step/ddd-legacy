package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import kitchenpos.testfixture.ProductTestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {

        //given
        Product request = ProductTestFixture.createProductRequest("후라이드치킨", 17000L);
        Product response = ProductTestFixture.createProduct("후라이드치킨", 17000L);

        given(productService.create(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.price").value(response.getPrice()));

    }

    @Test
    void changePrice() throws Exception {

        //given
        Product product = ProductTestFixture.createProduct("후라이드치킨", 18000L);
        Product response = ProductTestFixture.createProduct("후라이드치킨", 17000L);

        given(productService.changePrice(any(), any()))
                .willReturn(response);
        product.setPrice(BigDecimal.valueOf(17000));

        //when then
        mockMvc.perform(put("/api/products/{productId}/price", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(response.getPrice()));

    }

    @Test
    void findAll() throws Exception {

        //given
        Product product1 = ProductTestFixture.createProduct("후라이드치킨", 17000L);
        Product product2 = ProductTestFixture.createProduct("후라이드치킨", 18000L);

        given(productService.findAll())
                .willReturn(Arrays.asList(product1, product2));

        //when then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(product1.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[0].price").value(product1.getPrice()))
                .andExpect(jsonPath("$[1].id").value(product2.getId().toString()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()))
                .andExpect(jsonPath("$[1].price").value(product2.getPrice()));

    }
}