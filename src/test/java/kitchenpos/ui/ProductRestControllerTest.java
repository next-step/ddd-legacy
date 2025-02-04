package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import kitchenpos.fixture.ProductFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void 컨트롤러_상품_생성_성공_테스트() throws Exception {
        final UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        when(productService.create(any(Product.class))).thenReturn(ProductFixture.product(productId, "테스트 상품", new BigDecimal(10000)));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProductFixture.product(null, "테스트 상품", new BigDecimal(10000))))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("테스트 상품"))
                .andExpect(jsonPath("$.price").value(10000));
    }

    @Test
    void 상품_가격_변경_성공_테스트() throws Exception {
        final UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        when(productService.changePrice(eq(productId), any(Product.class))).thenReturn(ProductFixture.product(productId, "테스트 상품", new BigDecimal("18000")));

        mockMvc.perform(put("/api/products/" + productId + "/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProductFixture.product(productId, "테스트 상품", new BigDecimal("18000"))))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("테스트 상품"))
                .andExpect(jsonPath("$.price").value(new BigDecimal("18000")));
    }

    @Test
    void 상품_전체_찾기_성공_테스트() throws Exception {
        Product product = ProductFixture.product(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), "테스트 상품", new BigDecimal(15000));

        when(productService.findAll()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}