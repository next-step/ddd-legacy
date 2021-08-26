package kitchenpos.ui;

import static kitchenpos.KitchenposTestFixture.후라이드;
import static kitchenpos.KitchenposTestFixture.후라이드치킨_Menu;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.IntegrationTest;
import kitchenpos.ui.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ProductRestControllerTest extends IntegrationTest {

    ProductRequest 메론치킨;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        메론치킨 = new ProductRequest("메론치킨", 21000L);
    }

    @DisplayName("제품을 생성한다")
    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(메론치킨)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @DisplayName("제품 생성 실패 - 제품가격이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyPrice() throws Exception {
        메론치킨.setPrice(null);
        mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(메론치킨)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("제품 생성 실패 - 제품가격은 음수가 될 수 없다")
    @Test
    void createFailedByNegativePrice() throws Exception {
        메론치킨.setPrice(BigDecimal.valueOf(-1L));
        mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(메론치킨)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("제품 생성 실패 - 제품명이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyName() throws Exception {
        메론치킨.setName(null);
        mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(메론치킨)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("제품 생성 실패 - 제품명에 비속어가 포함되어선 안된다")
    @Test
    void createFailedByIncludingProfanity() throws Exception {
        메론치킨.setName("Shit melon chicken");
        mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(메론치킨)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("제품 가격을 변경한다")
    @Test
    void changePrice() throws Exception {
        ProductRequest request = new ProductRequest(18000L);
        mockMvc.perform(put("/api/products/{productId}/price", 후라이드.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price").value(request.price()));
    }

    @DisplayName("제품 가격 변경 실패 - 제품 가격이 반드시 전달되어야 한다")
    @Test
    void changePriceFailedByEmptyPrice() throws Exception {
        ProductRequest request = new ProductRequest();
        mockMvc.perform(put("/api/products/{productId}/price", 후라이드.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("제품 가격 변경 실패 - 제품가격은 음수가 될 수 없다")
    @Test
    void changePriceFailedByNegativePrice() throws Exception {
        ProductRequest request = new ProductRequest(-1L);
        mockMvc.perform(put("/api/products/{productId}/price", 후라이드.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("제품 가격 변경 실패 - 실제 존재하는 제품만 가격을 변경할 수 있다")
    @Test
    void changePriceFailedByNoSuchProduct() throws Exception {
        ProductRequest request = new ProductRequest(18000L);
        mockMvc.perform(put("/api/products/{productId}/price", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("제품 가격 변경 후 단품 가격의 합을 초과하는 메뉴가 숨김처리 된다")
    @Test
    void changePriceHidingMenuByExceedingTotalPrice() throws Exception {
        ProductRequest request = new ProductRequest(14000L);
        mockMvc.perform(put("/api/products/{productId}/price", 후라이드.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc.perform(put("/api/menus/{menuId}/display", 후라이드치킨_Menu.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("제품 목록을 가져온다")
    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
    }
}
