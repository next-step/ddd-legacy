package kitchenpos.ui;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.AfterEach;
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

  @AfterEach
  void tearDown() {
    productRepository.deleteAll();
  }

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

  @DisplayName("상품조회 요청에 응답으로 200 OK 응답과 함계 등록된 상품목록을 반환한다")
  @Test
  void givenProducts_whenFindAll_thenReturnProducts() throws Exception {
    Product product1 = new Product();
    product1.setId(UUID.randomUUID());
    product1.setName("후라이드치킨");
    product1.setPrice(BigDecimal.valueOf(11000));

    Product product2 = new Product();
    product2.setId(UUID.randomUUID());
    product2.setName("양념치킨");
    product2.setPrice(BigDecimal.valueOf(12000));

    productRepository.saveAll(List.of(product1, product2));

    mvc.perform(get("/api/products").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value(product1.getName()))
        .andExpect(jsonPath("$[0].price").value(product1.getPrice().longValue()))
        .andExpect(jsonPath("$[1].name").value(product2.getName()))
        .andExpect(jsonPath("$[1].price").value(product2.getPrice().longValue()));
  }

}
