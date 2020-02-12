package kitchenpos.controller;

import kitchenpos.bo.ProductBo;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductBo productBo;

    private static Product mockCreated;
    private static List<Product> mockProducts = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        mockCreated = new Product();
        mockCreated.setId(1L);

        mockProducts.add(new Product());
    }

    @DisplayName("POST 상품 시도 성공(201)")
    @Test
    public void postProductSuccess() throws Exception {
        given(productBo.create(any(Product.class))).willReturn(mockCreated);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\": \"강정치킨\",\n" +
                        "  \"price\": 17000\n" +
                        "}"))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/products/1"))
                .andExpect(content().string(containsString("\"id\":1")))
        ;
    }

    @DisplayName("GET 상품 콜렉션 시도 성공(200)")
    @Test
    public void getProductsSuccess() throws Exception {
        given(productBo.list()).willReturn(mockProducts);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("price")))
        ;
    }
}
