package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.bo.ProductBo;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductBo productBo;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상품 정상 생성")
    void create() throws Exception {
        String productName = "간장치킨";
        BigDecimal price = BigDecimal.valueOf(17000);

        Product product = createProduct(productName, price);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(productName))
                .andExpect(jsonPath("price").value("17000.0"))
        ;
    }

    @DisplayName("상품 가격이 0 보다 작을 경우 생성 실패")
    @ParameterizedTest
    @ValueSource(strings = "-1000")
    void createFailByNegative(BigDecimal price) {
        String productName = "양념치킨";
        Product product = createProduct(productName, price);

        assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(product)))
                    .andDo(print())
                    .andExpect(status().is5xxServerError())
            ;
        });
    }

    @DisplayName("상품 가격이 Null 일 경우 생성 실패")
    @ParameterizedTest
    @NullSource
    void createFailByNull(BigDecimal price) {
        Product product = createProduct("후라이드치킨", price);

        assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(product)))
                    .andDo(print())
                    .andExpect(status().is5xxServerError())
            ;
        });
    }

    @Test
    @DisplayName("상품 리스트 조회 테스트")
    void list() throws Exception {
        Product product1 = createProduct("양념치킨", BigDecimal.valueOf(17000));
        Product product2 = createProduct("후라이드치킨", BigDecimal.valueOf(16000));

        productBo.create(product1);
        productBo.create(product2);

        mockMvc.perform(get("/api/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").exists())
        ;

    }

    private Product createProduct(String productName, BigDecimal price) {
        Product product = new Product();

        product.setName(productName);
        product.setPrice(price);

        return product;
    }
}
