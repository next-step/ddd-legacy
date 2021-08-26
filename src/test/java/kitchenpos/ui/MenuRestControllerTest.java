package kitchenpos.ui;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static kitchenpos.KitchenposTestFixture.숨겨진비싼후라이드치킨;
import static kitchenpos.KitchenposTestFixture.한마리메뉴;
import static kitchenpos.KitchenposTestFixture.후라이드치킨_Menu;
import static kitchenpos.KitchenposTestFixture.후라이드치킨_후라이드;
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
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.ui.dto.MenuRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class MenuRestControllerTest extends IntegrationTest {

    MenuRequest 후라이드특가;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        후라이드특가 = new MenuRequest(
            "후라이드특가", 15000L, true, 한마리메뉴, 후라이드치킨_후라이드);
    }

    @DisplayName("메뉴를 생성한다")
    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @DisplayName("메뉴 생성 실패 - 메뉴가격이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyPrice() throws Exception {
        후라이드특가.setPrice(null);
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 생성 실패 - 메뉴가격은 음수가 될 수 없다")
    @Test
    void createFailedByNegativePrice() throws Exception {
        후라이드특가.setPrice(BigDecimal.valueOf(-1L));
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 생성 실패 - 실제 존재하는 메뉴 그룹에만 포함시킬 수 있다")
    @Test
    void createFailedByNoSuchMenuGroup() throws Exception {
        후라이드특가.setMenuGroup(new MenuGroup());
        후라이드특가.setMenuGroupId(UUID.randomUUID());
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 생성 실패 - 메뉴제품목록이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyMenuProducts() throws Exception {
        후라이드특가.setMenuProducts(emptyList());
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 생성 실패 - 메뉴제품목록에는 중복이 허용되지 않는다")
    @Test
    void createFailedByDuplicateMenuProducts() throws Exception {
        후라이드특가.setMenuProducts(asList(후라이드치킨_후라이드, 후라이드치킨_후라이드));
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 생성 실패 - 메뉴제품의 제품수량은 음수가 될 수 없다")
    @Test
    void createFailedByNegativeMenuProductQuantity() throws Exception {
        후라이드치킨_후라이드.setQuantity(-1L);
        후라이드특가.setMenuProducts(singletonList(후라이드치킨_후라이드));
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 생성 실패 - 메뉴제품으로는 실제 존재하는 제품만 등록된다")
    @Test
    void createFailedByNoSuchProduct() throws Exception {
        후라이드치킨_후라이드.setProduct(new Product());
        후라이드치킨_후라이드.setProductId(UUID.randomUUID());
        후라이드특가.setMenuProducts(singletonList(후라이드치킨_후라이드));
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 생성 실패 - 메뉴가격이 메뉴제품목록의 총 가격보다 크면 안된다")
    @Test
    void createFailedByExceedingTotalPrice() throws Exception {
        후라이드특가.setPrice(BigDecimal.valueOf(16001L));
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 생성 실패 - 메뉴명이 반드시 전달되어야 한다")
    @Test
    void createFailedByEmptyMenuName() throws Exception {
        후라이드특가.setName(null);
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 생성 실패 - 메뉴명에 비속어가 포함되어서는 안된다")
    @Test
    void createFailedByIncludingProfanity() throws Exception {
        후라이드특가.setName("Shit Chicken");
        mockMvc.perform(post("/api/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(후라이드특가)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴가격을 수정한다")
    @Test
    void changePrice() throws Exception {
        MenuRequest request = new MenuRequest(10000L);
        mockMvc.perform(put("/api/menus/{menuId}/price", 후라이드치킨_Menu.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price").value(request.price()));
    }

    @DisplayName("메뉴가격 수정 실패 - 메뉴가격이 반드시 전달되어야 한다")
    @Test
    void changePriceFailedByEmptyPrice() throws Exception {
        MenuRequest request = new MenuRequest();
        mockMvc.perform(put("/api/menus/{menuId}/price", 후라이드치킨_Menu.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());

    }

    @DisplayName("메뉴가격 수정 실패 - 메뉴가격은 음수가 될 수 없다")
    @Test
    void changePriceFailedByNegativePrice() throws Exception {
        MenuRequest request = new MenuRequest(-1L);
        mockMvc.perform(put("/api/menus/{menuId}/price", 후라이드치킨_Menu.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴가격 수정 실패 - 실제 존재하는 메뉴만 수정할 수 있다")
    @Test
    void changePriceFailedByNoSuchMenu() throws Exception {
        MenuRequest request = new MenuRequest(-1L);
        mockMvc.perform(put("/api/menus/{menuId}/price", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴가격 수정 실패 - 메뉴가격이 단품가격의 총 합보다 크면 안된다")
    @Test
    void changePriceFailedByExceedingTotalPrice() throws Exception {
        MenuRequest request = new MenuRequest(16001L);
        mockMvc.perform(put("/api/menus/{menuId}/price", 후라이드치킨_Menu.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴를 노출한다")
    @Test
    void display() throws Exception {
        mockMvc.perform(put("/api/menus/{menuId}/display", 후라이드치킨_Menu.getId()))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("메뉴 노출 실패 - 실제 존재하는 메뉴만 노출할 수 있다")
    @Test
    void displayFailedByNoSuchMenu() throws Exception {
        mockMvc.perform(put("/api/menus/{menuId}/display", UUID.randomUUID()))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 노출 실패 - 메뉴가격이 단품가격의 총 합보다 크면 안된다")
    @Test
    void displayFailedByExceedingTotalPrice() throws Exception {
        mockMvc.perform(put("/api/menus/{menuId}/display", 숨겨진비싼후라이드치킨.getId()))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @DisplayName("메뉴를 숨긴다")
    @Test
    void hide() throws Exception {
        mockMvc.perform(put("/api/menus/{menuId}/hide", 후라이드치킨_Menu.getId()))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("메뉴 숨기기 실패 - 실제 존재하는 메뉴만 숨길 수 있다")
    @Test
    void hideFailedByNoSuchMenu() throws Exception {
        mockMvc.perform(put("/api/menus/{menuId}/hide", UUID.randomUUID()))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 목록을 조회한다")
    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/api/menus"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
    }
}
