package kitchenpos.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProductRestControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProductRepository productRepository;

  @DisplayName("유효한 상품명과 가격을 통한 상품등록 요청에 응답으로 HTTP 201 상태값과 함께 등록된 상품을 반환한다")
  @Test
  void givenValidProduct_whenCreateProduct_thenStatus201WithCratedProduct() throws Exception {
    Product product = new Product();
    product.setName("후라이드치킨");
    product.setPrice(BigDecimal.valueOf(11000));

    mvc.perform(
        post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(product)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value(product.getName()))
        .andExpect(jsonPath("$.price").value(product.getPrice()));
  }

  @DisplayName("유효한 상품가격변경 요청에 응답으로 HTTP 200 상태값과 함께 변경된 상품을 반환한다")
  @Test
  void givenValidChangeProduct_whenChangeProduct_thenStatus200WithChangedProduct() throws Exception {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("후라이드치킨");
    product.setPrice(BigDecimal.valueOf(11000));
    Product savedProduct = productRepository.save(product);

    Product changePriceProduct = new Product();
    changePriceProduct.setPrice(BigDecimal.valueOf(12000));

    mvc.perform(
            put("/api/products/{productId}/price", savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePriceProduct)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value(product.getName()))
        .andExpect(jsonPath("$.price").value(changePriceProduct.getPrice()));
  }

}
