package kitchenpos.ui;

import kitchenpos.BaseControllerTest;
import kitchenpos.commons.ProductGenerator;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductRestControllerTest extends BaseControllerTest {

    @Autowired
    private ProductGenerator productGenerator;

    @Test
    @DisplayName("제품 등록 - 성공")
    void createMenuGroup() throws Exception {
        // given
        String name = "product 1";
        BigDecimal price = BigDecimal.valueOf(1000);
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("price").value(price))
        ;
    }

    @Test
    @DisplayName("제품 가격 수정")
    void changePrice() throws Exception {
        // given
        Product product = productGenerator.generate();
        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(500));

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/products/{productId}/price", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("price").value(request.getPrice().intValue()))
        ;
    }

    @Test
    @DisplayName("모든 제품 리스트 조회 - 성공")
    void findAllProduct() throws Exception {
        // given
        int size = 10;
        List<Product> products = IntStream.range(1, size).mapToObj(i -> productGenerator.generate()).collect(Collectors.toList());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/products")
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..['id']").exists())
                .andExpect(jsonPath("$..['name']").exists())
                .andExpect(jsonPath("$..['price']").exists())
        ;
    }
}