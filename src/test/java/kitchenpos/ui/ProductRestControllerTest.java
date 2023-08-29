package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.application.ProductService;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Test
    @DisplayName("상품 생성 API")
    void create() throws Exception {
        var request = TestFixture.createProduct("테스트", 1000L);
        var response = TestFixture.copy(request);
        given(productService.create(any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.price").value(response.getPrice()))
                .andReturn();
    }

    @Test
    @DisplayName("상품 가격 변경 API")
    void changePrice() throws Exception {
        var productId = UUID.randomUUID();
        var response = TestFixture.createProduct(productId, "테스트", 1000L);
        given(productService.changePrice(any(), any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/products/{productId}/price", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(response))
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.price").value(response.getPrice()))
                .andReturn();
    }

    @Test
    @DisplayName("상품 목록 조회 API")
    void findAll() throws Exception {
        var response = List.of(
                TestFixture.createProduct("테스트1", 1000L),
                TestFixture.createProduct("테스트2", 2000L)
        );

        given(productService.findAll()).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andReturn();
    }
}
