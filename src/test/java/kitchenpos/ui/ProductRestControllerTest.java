package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.DummyData;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest extends DummyData {

    @Autowired
    private MockMvc webMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ProductService productService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void utf8Filter() {
        this.webMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @DisplayName("상품 생성하기")
    @Test
    void createProduct() throws Exception {
        Product product = new Product();
        product.setName(products.get(0).getName());
        product.setPrice(products.get(0).getPrice());

        given(productService.create(any())).willReturn(products.get(0));

        ResultActions perform = webMvc.perform(
                post("/api/products")
                        .content(objectMapper.writeValueAsString(product))
                        .contentType("application/json")
        );

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
        Product product = products.get(0);
        Product changePrice = new Product();
        changePrice.setPrice(ofPrice(2000));

        given(productService.changePrice(any(), any())).willReturn(product);

        ResultActions perform = webMvc.perform(
                put("/api/products/{productId}/price", product.getId())
                        .content(objectMapper.writeValueAsString(changePrice))
                        .contentType("application/json")
        );

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