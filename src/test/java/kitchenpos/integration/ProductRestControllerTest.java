package kitchenpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.config.IntegrationTest;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.util.MockMvcUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.fixture.ProductFixture.createProduct;
import static kitchenpos.fixture.ProductFixture.createProductWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@IntegrationTest
class ProductRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PurgomalumClient purgomalumClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void create() throws Exception {
        final MvcResult result = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProduct("후라이드 치킨", BigDecimal.valueOf(16000)))))
                .andReturn();

        final Product product = MockMvcUtil.readValue(objectMapper, result, Product.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(result.getResponse().getHeader("Location")).isEqualTo("/api/products/" + product.getId()),
                () -> assertThat(product.getId()).isNotNull(),
                () -> assertThat(product.getName()).isEqualTo("후라이드 치킨"),
                () -> assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(16000))
        );
    }

    @Test
    void changePrice() throws Exception {
        final Product changedProduct = productRepository.save(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000)));
        changedProduct.setPrice(BigDecimal.valueOf(15000));

        final MvcResult result = mockMvc.perform(put("/api/products/" + changedProduct.getId() + "/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changedProduct)))
                .andReturn();

        final Product product = MockMvcUtil.readValue(objectMapper, result, Product.class);

        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(15000))
        );
    }

    @Test
    void findAll() throws Exception {
        final Product product1 = productRepository.save(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000)));
        final Product product2 = productRepository.save(createProductWithId("양념 치킨", BigDecimal.valueOf(16000)));

        // when
        final MvcResult result = mockMvc.perform(get("/api/products"))
                .andReturn();

        final List<Product> products = MockMvcUtil.readListValue(objectMapper, result, Product.class);

        // then
        assertAll(
                () -> assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(products).hasSize(2),
                () -> assertThat(products.stream().map(Product::getId)).contains(product1.getId(), product2.getId())
        );
    }
}
