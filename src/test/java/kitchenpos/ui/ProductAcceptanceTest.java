package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
public class ProductAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void create() throws Exception {

        //given
        Product product = new Product();
        product.setName("맛있는 미트파이");
        product.setPrice(BigDecimal.valueOf(1000L));

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product))
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());


    }

/*
    @PutMapping("/{productId}/price")
    public ResponseEntity<Product> changePrice(@PathVariable final UUID productId, @RequestBody final Product request) {
        return ResponseEntity.ok(productService.changePrice(productId, request));
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }
*/

}
