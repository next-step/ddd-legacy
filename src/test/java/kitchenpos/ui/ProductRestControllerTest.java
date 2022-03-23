package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import kitchenpos.application.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static kitchenpos.domain.ProductFixture.FRIED_CHICKEN;
import static kitchenpos.domain.ProductFixture.HONEY_COMBO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {

    public static final String BASE_URL = "/api/products/";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("상품 생성 요청 성공")
    void productCreateRequestSuccess() throws Exception {
        // given
        given(productService.create(any())).willReturn(FRIED_CHICKEN);

        // when
        mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(FRIED_CHICKEN)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(FRIED_CHICKEN.getId().toString()))
               .andExpect(jsonPath("$.name").value(FRIED_CHICKEN.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("상품 가격 변경 요청 성공")
    void modifyProductPriceRequestSuccess() throws Exception {
        // given
        given(productService.changePrice(any(), any())).willReturn(FRIED_CHICKEN);

        // when
        mockMvc.perform(put(BASE_URL + FRIED_CHICKEN.getId().toString() + "/price")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(FRIED_CHICKEN)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(FRIED_CHICKEN.getId().toString()))
               .andExpect(jsonPath("$.name").value(FRIED_CHICKEN.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("상품 목록 요청 성공")
    void getProductsRequestSuccess() throws Exception {
        // given
        given(productService.findAll()).willReturn(Arrays.asList(FRIED_CHICKEN, HONEY_COMBO));

        // when
        mockMvc.perform(get(BASE_URL))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(FRIED_CHICKEN.getId().toString()))
               .andExpect(jsonPath("$[0].name").value(FRIED_CHICKEN.getName()))
               .andExpect(jsonPath("$[1].id").value(HONEY_COMBO.getId().toString()))
               .andExpect(jsonPath("$[1].name").value(HONEY_COMBO.getName()))
               .andDo(print());
    }
}

