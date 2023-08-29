package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.fixture.ProductFixtures.createProduct;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void 상품을_생성한다() throws Exception {
        //given
        Product product = createProduct("상품1", new BigDecimal("1000"));
        given(productService.create(any()))
                .willReturn(product);

        //when
        ResultActions result = mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)));

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice()));
    }

    @Test
    void 상품_가격을_변경한다() throws Exception {
        //given
        Product product = createProduct("상품1", new BigDecimal("2000"));
        given(productService.changePrice(any(), any()))
                .willReturn(product);

        //when
        ResultActions result = mvc.perform(put("/api/products/{productId}/price", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(product.getPrice()));
    }

    @Test
    void 모든_상품을_조회한다() throws Exception {
        //given
        Product product1 = createProduct("상품1", new BigDecimal("1000"));
        Product product2 = createProduct("상품2", new BigDecimal("2000"));

        List<Product> products = List.of(product1, product2);

        given(productService.findAll())
                .willReturn(products);

        //when
        ResultActions result = mvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(products)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(products.size())))
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[0].price").value(product1.getPrice()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()))
                .andExpect(jsonPath("$[1].price").value(product2.getPrice()));

    }

}