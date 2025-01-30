package kitchenpos.ui;

import static kitchenpos.builder.TestFixtureFactory.createProduct;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 생성한다")
    void create_product() throws Exception {
        // given
        Product request = createProductRequest();

        // when
        ResultActions result = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("김치"))
                .andExpect(jsonPath("$.price").value(5000));
    }

    @Test
    @DisplayName("상품의 가격을 변경한다")
    void change_productPrice() throws Exception {
        // given
        Product request = createAndSaveProduct("김치", 5000);
        request.setPrice(BigDecimal.valueOf(6000));

        // when
        ResultActions result = mockMvc.perform(put("/api/products/{productId}/price", request.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId().toString()))
                .andExpect(jsonPath("$.price").value(6000));
    }

    @Test
    @DisplayName("상품 목록을 조회한다")
    void find_allProducts() throws Exception {
        // given
        createAndSaveProduct("김치", 5000);
        createAndSaveProduct("호박", 1000);

        // when
        ResultActions result = mockMvc.perform(get("/api/products"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private Product createProductRequest() {
        return new Product("김치", BigDecimal.valueOf(5000));
    }

    private Product createAndSaveProduct(String name, int price) {
        Product product = createProduct(name, price);
        return productRepository.save(product);
    }
}
