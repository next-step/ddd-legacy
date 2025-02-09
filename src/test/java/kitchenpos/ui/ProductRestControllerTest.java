package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void create() throws Exception {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("product1");
        product.setPrice(BigDecimal.valueOf(10000));
        given(productService.create(any())).willReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.id", is(product.getId().toString())))
                .andExpect(jsonPath("$.price", is(product.getPrice().intValue())))
                .andExpect(header().string("Location", "/api/products/" + product.getId().toString()));
    }

    @Test
    void changePrice() throws Exception {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(10000));

        given(productService.changePrice(any(), any())).willReturn(product);
        product.setPrice(BigDecimal.valueOf(20000));

        mockMvc.perform(put("/api/products/" + product.getId().toString() + "/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().toString())))
                .andExpect(jsonPath("$.price", is(product.getPrice().intValue())));
    }

    @Test
    void findAll() throws Exception {
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        products.add(product1);
        products.add(product2);

        given(productService.findAll()).willReturn(products);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(product1.getId().toString())))
                .andExpect(jsonPath("$[1].id", is(product2.getId().toString())));
    }
}
