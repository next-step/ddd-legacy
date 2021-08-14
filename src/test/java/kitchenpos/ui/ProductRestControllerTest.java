package kitchenpos.ui;

import kitchenpos.FixtureData;
import kitchenpos.MockMvcSupport;
import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest extends MockMvcSupport {

    @Autowired
    private MockMvc webMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        fixtureProducts();

        this.webMvc = ofUtf8MockMvc();
    }

    @DisplayName("상품 생성하기")
    @Test
    void createProduct() throws Exception {
        // given
        Product product = new Product();
        product.setName(products.get(0).getName());
        product.setPrice(products.get(0).getPrice());

        given(productService.create(any())).willReturn(products.get(0));

        // when
        ResultActions perform = webMvc.perform(
                post("/api/products")
                        .content(objectMapper.writeValueAsString(product))
                        .contentType("application/json")
        );

        // then
        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.name").value(product.getName()));
    }

    @DisplayName("가격 변경")
    @Test
    void changePrice() throws Exception {
        // given
        Product changePrice = new Product();
        changePrice.setPrice(ofPrice(2000));

        Product product = products.get(0);
        product.setPrice(changePrice.getPrice());

        given(productService.changePrice(any(), any())).willReturn(product);

        // when
        ResultActions perform = webMvc.perform(
                put("/api/products/{productId}/price", product.getId())
                        .content(objectMapper.writeValueAsString(changePrice))
                        .contentType("application/json")
        );

        // then
        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.name").value(product.getName()));
    }

    @DisplayName("상품 전체조회")
    @Test
    void findAll() throws Exception {
        given(productService.findAll()).willReturn(products);

        webMvc.perform(get("/api/products"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}