package kitchenpos.ui;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static kitchenpos.fixture.ProductFixture.TEST_PRODUCT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductRestController.class)
@DisplayName("/api/products 상품 ui 레이어 테스트")
class ProductRestControllerTest extends BaseRestControllerTest {

    @MockBean
    ProductService productService;

    private static final String BASE_URL = "/api/products";

    @Test
    @DisplayName("[POST] 상품을 등록한다.")
    void createTest() throws Exception {
        //given
        Product product = TEST_PRODUCT();
        given(productService.create(any())).willReturn(product);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("price").exists())
        ;
    }

    @Test
    @DisplayName("[PUT] /{productId}/price 상품의 가격을 변경한다.")
    void changeTest() throws Exception {
        //given
        Product product = TEST_PRODUCT();
        given(productService.changePrice(any(), any(Product.class))).willReturn(product);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL + "/" + product.getId() + "/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("price").exists())
        ;
    }
}