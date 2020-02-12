package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.bo.ProductBo;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductBo productBo;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("상품을 생성할 수 있어야 한다.")
    @Test
    void create() throws Exception {
        // given
        Product requestProduct = createUnregisteredProductWithName("Test Product");
        Product responseProduct = createRegisteredProductWithId(requestProduct, new Random().nextLong());

        given(productBo.create(any(Product.class)))
                .willReturn(responseProduct);

        // when
        ResultActions result = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestProduct)));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/products/" + responseProduct.getId()))
                .andExpect(content().json(objectMapper.writeValueAsString(responseProduct)));
    }

    @DisplayName("상품 목록을 볼 수 있어야 한다.")
    @Test
    void list() throws Exception {
        // given
        Product product1 = createRegisteredProductWithId(1L);
        Product product2 = createRegisteredProductWithId(2L);

        given(productBo.list())
                .willReturn(Arrays.asList(product1, product2));

        // when
        ResultActions result = mockMvc.perform(get("/api/products"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(product1.getName())))
                .andExpect(content().string(containsString(product2.getName())));
    }

    private Product createUnregisteredProductWithName(String name) {
        return new Product() {{
            setName(name);
            setPrice(BigDecimal.valueOf(3000));
        }};
    }

    private Product createRegisteredProductWithId(Long productId) {
        Product product = createUnregisteredProductWithName("Product " + productId);
        product.setId(productId);

        return product;
    }

    private Product createRegisteredProductWithId(Product unregisteredProduct, Long productId) {
        return new Product() {{
            setId(productId);
            setName(unregisteredProduct.getName());
            setPrice(unregisteredProduct.getPrice());
        }};
    }
}
