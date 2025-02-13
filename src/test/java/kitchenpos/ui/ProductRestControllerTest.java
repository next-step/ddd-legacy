package kitchenpos.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.ProductService;
import kitchenpos.config.TestConfig;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductRestController.class)
@Import(TestConfig.class)
class ProductRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProductService productService;

    @DisplayName("상품 목록 조회")
    @Nested
    class ListProducts {

        @DisplayName("상품 목록 조회 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeeds_then_responds_200_ok() throws Exception {
            // given
            given(productService.findAll()).willReturn(List.of());

            // when
            mockMvc.perform(
                            get("/api/products"))
                    // then
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("상품 생성")
    @Nested
    class CreateProduct {

        @DisplayName("상품이 생성 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var name = "";
            var price = 0;
            var body = new HashMap<String, Object>() {{
                put("name", name);
                put("price", price);
            }};
            var content = objectMapper.writeValueAsString(body);
            given(productService.create(any())).willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(
                            post("/api/products")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("상품이 생성 성공하면 201 Created를 응답한다.")
        @Test
        void if_succeeds_then_responds_201_created() throws Exception {
            // given
            var name = "상품";
            var price = 1000;
            var body = new HashMap<String, Object>() {{
                put("name", name);
                put("price", price);
            }};
            var content = objectMapper.writeValueAsString(body);
            var product = new Product();
            product.setId(UUID.randomUUID());
            product.setName("상품");
            given(productService.create(any())).willReturn(product);

            // when
            mockMvc.perform(
                            post("/api/products")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isCreated());
        }
    }

    @DisplayName("상품 가격 변경")
    @Nested
    class ChangeProductPrice {

        @DisplayName("상품의 가격 변경 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var price = 1000;
            var body = new HashMap<String, Object>() {{
                put("price", price);
            }};
            var content = objectMapper.writeValueAsString(body);
            var productId = UUID.randomUUID();
            given(productService.changePrice(any(), any())).willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(
                            put("/api/products/{productId}/price", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("상품의 가격 변경 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeeds_then_responds_200_ok() throws Exception {
            // given
            var price = 1000;
            var body = new HashMap<String, Object>() {{
                put("price", price);
            }};
            var content = objectMapper.writeValueAsString(body);
            var product = new Product();
            var productId = UUID.randomUUID();
            product.setId(UUID.randomUUID());
            product.setName("상품");
            given(productService.changePrice(any(), any())).willReturn(product);

            // when
            mockMvc.perform(
                            put("/api/products/{productId}/price", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isOk());
        }
    }
}
